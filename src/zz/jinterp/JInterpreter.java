/*
TOD - Trace Oriented Debugger.
Copyright (c) 2006-2008, Guillaume Pothier
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this 
      list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, 
      this list of conditions and the following disclaimer in the documentation 
      and/or other materials provided with the distribution.
    * Neither the name of the University of Chile nor the names of its contributors 
      may be used to endorse or promote products derived from this software without 
      specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.

Parts of this work rely on the MD5 algorithm "derived from the RSA Data Security, 
Inc. MD5 Message-Digest Algorithm".
*/
package zz.jinterp;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JClass_java_lang_Class.Instance;
import zz.jinterp.JNormalBehavior.JFrame;
import zz.jinterp.JPrimitive.JBoolean;
import zz.jinterp.JPrimitive.JByte;
import zz.jinterp.JPrimitive.JChar;
import zz.jinterp.JPrimitive.JDouble;
import zz.jinterp.JPrimitive.JFloat;
import zz.jinterp.JPrimitive.JInt;
import zz.jinterp.JPrimitive.JLong;
import zz.jinterp.JPrimitive.JShort;
import zz.jinterp.SimpleInterp.SimpleArray;
import zz.jinterp.SimpleInterp.SimpleInstance;

/**
 * A brain-dead simple java interpreter.
 * @param <O> Type of Java objects
 * @author gpothier
 */
public abstract class JInterpreter
{
	/**
	 * Utility constant that can be used to call no-args methods.
	 */
	public static final JObject[] NOARGS = {};
	
	private Map<String, JClass> itsLoadedClasses;
	private Map<JType, JClass_java_lang_Class.Instance> itsLoadedMetaclasses;
	private JClass_java_lang_Object itsObjectClass;
	private JClass_java_lang_Class itsMetaclassClass;

	private void checkInit()
	{
		if (itsLoadedClasses == null) init();
	}
	
	private void init()
	{
		itsLoadedClasses = new HashMap<String, JClass>();
		itsLoadedMetaclasses = new HashMap<JType, Instance>();
		itsObjectClass = new JClass_java_lang_Object(this);
		itsMetaclassClass = new JClass_java_lang_Class(this, itsObjectClass);
		
		// Load special classes
		itsLoadedClasses.put(JClass_java_lang_Object.NAME, itsObjectClass);
		itsLoadedClasses.put(JClass_java_lang_Class.NAME, itsMetaclassClass);
		
		loadNativeClass(JClass_java_lang_System.NAME);
		loadNativeClass(JClass_java_lang_Float.NAME);
		loadNativeClass(JClass_java_lang_Double.NAME);
		loadNativeClass(JClass_java_lang_String.NAME);
		loadNativeClass(JClass_java_io_FileSystem.NAME);
		loadNativeClass(JClass_java_io_UnixFileSystem.NAME);
		loadNativeClass(JClass_java_lang_Throwable.NAME);
		loadNativeClass(JClass_java_security_AccessController.NAME);
		loadNativeClass(JClass_sun_reflect_ReflectionFactory$GetReflectionFactoryAction.NAME);
		loadNativeClass(JClass_sun_reflect_ReflectionFactory.NAME);
		loadNativeClass(JClass_java_io_ObjectStreamClass.NAME);
	}
	
	/**
	 * Retrieves the bytecode of the given class.
	 * @param aName Class name in JVM notation
	 */
	protected abstract byte[] getClassBytecode(String aName);
	
	public JClass getClass(String aName)
	{
		checkInit();
		JClass theClass = itsLoadedClasses.get(aName);
		if (theClass == null)
		{
			byte[] theBytecode = getClassBytecode(aName);
			theClass = new JNormalClass(this, JNormalClass.readClass(theBytecode));
			itsLoadedClasses.put(aName, theClass);
			theClass.init();
		}
		return theClass;
	}
	
	protected void loadNativeClass(String aName)
	{
		checkInit();
		byte[] theBytecode = getClassBytecode(aName);
		ClassNode theClassNode = JNormalClass.readClass(theBytecode);
		try
		{
			Class theClass = Class.forName("zz.jinterp.JClass_"+aName.replace('/', '_'));
			Constructor theConstructor = theClass.getConstructor(JInterpreter.class, ClassNode.class);
			JClass theInstance = (JClass) theConstructor.newInstance(this, theClassNode);
			itsLoadedClasses.put(aName, theInstance);
			theInstance.init();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public Instance getMetaclass(JType aType)
	{
		checkInit();
		Instance theInstance = itsLoadedMetaclasses.get(aType);
		if (theInstance == null)
		{
			theInstance = new Instance(itsMetaclassClass, aType);
			itsLoadedMetaclasses.put(aType, theInstance);
		}
		return theInstance;
	}
	

	public JBehavior getVirtual(String aClassName, String aMethodName, String aSignature)
	{
		JClass theClass = getClass(aClassName);
		return theClass.getVirtualBehavior(aMethodName, aSignature);
	}
	
	/**
	 * Creates a static field descriptor.
	 * This method can be overridden by subclasses if they need
	 * some special handling of static field values (in the same way the can
	 * subclass JInstance).
	 */
	public abstract JStaticField createStaticField(JClass aClass, String aName, JType aType, int aAccess);

	/**
	 * Executes a method
	 * @param aClassName Class name in JVM notation
	 * @param aMethodName Name of the method to invoke 
	 * @param aSignature Signature of the method in JVM notation
	 * @param aThis Context object
	 * @param aArgs Arguments to pass to the method
	 * @return The result of the call
	 */
	public JObject invoke(
			JFrame aParentFrame,
			String aClassName, 
			String aMethodName, 
			String aSignature, 
			JObject aThis, 
			JObject... aArgs)
	{
		JBehavior theBehavior = getVirtual(aClassName, aMethodName, aSignature);
		return theBehavior.invoke(aParentFrame, aThis, aArgs);
	}
	
	public JInstance instantiate(
			JFrame aParentFrame,
			String aClassName, 
			String aSignature, 
			JObject... aArgs)
	{
		JClass theClass = getClass(aClassName);
		JInstance theInstance = theClass.newInstance();
		
		JBehavior theBehavior = theClass.getBehavior("<init>", aSignature);
		theBehavior.invoke(aParentFrame, theInstance, aArgs);
		
		return theInstance;
	}
	
	public JInstance new_ClassCastException(JFrame aParentFrame, String aMessage)
	{
		return new_Exception("ClassCastException", aParentFrame, aMessage);
	}
	
	public JInstance new_NullPointerException(JFrame aParentFrame, String aMessage)
	{
		return new_Exception("NullPointerException", aParentFrame, aMessage);
	}
	
	public JInstance new_Exception(String aNQClassName, JFrame aParentFrame, String aMessage)
	{
		return instantiate(
				aParentFrame, 
				"java/lang/"+aNQClassName, 
				"(Ljava/lang/String;)V", 
				toJString(aMessage));
	}
	
	public JType getType(String aDescriptor)
	{
		Type theASMType = Type.getType(aDescriptor);
		return getType(theASMType);
	}
	
	protected JType getType(Type aASMType)
	{
        switch (aASMType.getSort()) {
        case Type.VOID: return JPrimitiveType.VOID;
        case Type.BOOLEAN: return JPrimitiveType.BOOLEAN;
        case Type.CHAR: return JPrimitiveType.CHAR;
        case Type.BYTE: return JPrimitiveType.BYTE;
        case Type.SHORT: return JPrimitiveType.SHORT;
        case Type.INT: return JPrimitiveType.INT;
        case Type.FLOAT: return JPrimitiveType.FLOAT;
        case Type.LONG: return JPrimitiveType.LONG;
        case Type.DOUBLE: return JPrimitiveType.DOUBLE;
        case Type.ARRAY: return new JArrayType(aASMType.getDimensions(), getType(aASMType.getElementType()));
        case Type.OBJECT: return getClass(aASMType.getClassName());
        	
        default:
        	throw new RuntimeException("Not handled: "+aASMType);
        }
	}
	
	public JObject[] toJObjects(Object... aObjects)
	{
		JObject[] theResult = new JObject[aObjects.length];
		for (int i=0;i<aObjects.length;i++) theResult[i] = toJObject(aObjects[i]);
		return theResult;
	}
	
	public JObject toJObject(Object aObject)
	{
		if (aObject instanceof Integer)
		{
			return new JInt((Integer) aObject);
		}
		else if (aObject instanceof Long)
		{
			return new JLong((Long) aObject);
		}
		else if (aObject instanceof Double)
		{
			return new JDouble((Double) aObject);
		}
		else if (aObject instanceof Float)
		{
			return new JFloat((Float) aObject);
		}
		else if (aObject instanceof String)
		{
			return toJString((String) aObject);
		}
		else if (aObject instanceof Boolean)
		{
			return new JBoolean((Boolean) aObject);
		}
		else if (aObject instanceof Short)
		{
			return new JShort((Short) aObject);
		}
		else if (aObject instanceof Byte)
		{
			return new JByte((Byte) aObject);
		}
		else if (aObject instanceof Character)
		{
			return new JChar((Character) aObject);
		}
		else throw new IllegalArgumentException(""+aObject);
	}
	
	public JInstance toJString(String aString)
	{
		JClass theClass = getClass("java/lang/String");
		SimpleInstance theInstance = new SimpleInstance(theClass);
		
		JField fOffset = theClass.getField("offset");
		JField fCount = theClass.getField("count");
		JField fValue = theClass.getField("value");
		JField fHash = theClass.getField("hash");
		
		theInstance.putFieldValue(fOffset, JInt._0);
		theInstance.putFieldValue(fCount, new JInt(aString.length()));
		theInstance.putFieldValue(fHash, new JInt(aString.hashCode()));
		
		JArray theValue = new SimpleArray(aString.length());
		for(int i=0;i<aString.length();i++) theValue.set(i, new JChar(aString.charAt(i)));
		theInstance.putFieldValue(fValue, theValue);
		
		return theInstance;
	}
	
	/**
	 * Converts a JInterpreter String to a Java String.
	 */
	public String toString(JInstance aInstance)
	{
		JClass theClass = getClass("java/lang/String");
		if (aInstance.getType() != theClass) throw new RuntimeException("Not a String: "+aInstance);
		
		JField fOffset = theClass.getField("offset");
		JField fCount = theClass.getField("count");
		JField fValue = theClass.getField("value");

		JInt theOffset = (JInt) aInstance.getFieldValue(fOffset);
		JInt theCount = (JInt) aInstance.getFieldValue(fCount);
		JArray theValue = (JArray) aInstance.getFieldValue(fValue);
		
		char[] theChars = new char[theCount.v];
		for(int i=0;i<theCount.v;i++) theChars[i] = ((JChar) theValue.get(i+theOffset.v)).v;
		
		return new String(theChars);
	}
	
	/**
	 * Returns the object to use as an implementation of java.io.FileSystem.
	 */
	public abstract JInstance getFileSystem(JFrame aParentFrame);
	public abstract JInstance getReflectionFactory(JFrame aParentFrame);
}
