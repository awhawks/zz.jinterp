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

import zz.jinterp.JNormalBehavior.JFrame;
import zz.jinterp.JPrimitive.JBoolean;



public class JClass_java_lang_Class extends JReflectiveClass
{
	public static final String NAME = "java/lang/Class";
	
	public JClass_java_lang_Class(JInterpreter aInterpreter, JClass aSuperClass)
	{
		super(aInterpreter, aSuperClass, new JClass[] {
				aInterpreter.getClass("java/io/Serializable"),
				aInterpreter.getClass("java/lang/reflect/GenericDeclaration"),
				aInterpreter.getClass("java/lang/reflect/Type"),
				aInterpreter.getClass("java/lang/reflect/AnnotatedElement"),
		});
	}
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public JInstance newInstance()
	{
		throw new UnsupportedOperationException();
	}
	
    static Class __getPrimitiveClass(String name) { return null; };
	public JObject _getPrimitiveClass(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		String theName = getInterpreter().toString((JInstance) aArgs[0]);
		if ("boolean".equals(theName)) return getInterpreter().getMetaclass(JPrimitiveType.BOOLEAN);
		else if ("byte".equals(theName)) return getInterpreter().getMetaclass(JPrimitiveType.BYTE);
		else if ("char".equals(theName)) return getInterpreter().getMetaclass(JPrimitiveType.CHAR);
		else if ("double".equals(theName)) return getInterpreter().getMetaclass(JPrimitiveType.DOUBLE);
		else if ("float".equals(theName)) return getInterpreter().getMetaclass(JPrimitiveType.FLOAT);
		else if ("int".equals(theName)) return getInterpreter().getMetaclass(JPrimitiveType.INT);
		else if ("long".equals(theName)) return getInterpreter().getMetaclass(JPrimitiveType.LONG);
		else if ("short".equals(theName)) return getInterpreter().getMetaclass(JPrimitiveType.SHORT);
		else throw new RuntimeException("Not handled: "+theName);
	}

    public boolean __desiredAssertionStatus() { return false; }
	public JObject _desiredAssertionStatus(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		return JBoolean._false;
	}
	
	public boolean __isArray() { return false; }
	public JObject _isArray(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		Instance theTarget = (Instance) aTarget;
		return (theTarget.getDelegate() instanceof JArrayType) ? JBoolean._true : JBoolean._false; 
	}
	
	public boolean __isPrimitive() { return false; }
	public JObject _isPrimitive(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		Instance theTarget = (Instance) aTarget;
		return (theTarget.getDelegate() instanceof JPrimitiveType) ? JBoolean._true : JBoolean._false; 
	}
	
	public Class __getComponentType() { return null; }
	public JObject _getComponentType(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		Instance theTarget = (Instance) aTarget;
		JArrayType theType = (JArrayType) theTarget.getDelegate();
		return getInterpreter().getMetaclass(theType.getElementType());
	}
	
	public static class Instance extends JInstance
	{
		private final JType itsDelegate;

		public Instance(JClass aClass, JType aDelegate)
		{
			super(aClass);
			itsDelegate = aDelegate;
		}

		public JType getDelegate()
		{
			return itsDelegate;
		}

		@Override
		public JObject getFieldValue(JField aField)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void putFieldValue(JField aField, JObject aValue)
		{
			throw new UnsupportedOperationException();
		}
	}
}
