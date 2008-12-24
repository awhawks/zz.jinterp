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

import java.util.HashMap;
import java.util.Map;

import zz.jinterp.SimpleInterp.SimpleInstance;


/**
 * Represents a Java class in the interpreter.
 * @author gpothier
 */
public abstract class JClass extends JType
{
	private final JInterpreter itsInterpreter;
	private final JClass itsSuperclass;
	
	private final Map<String, JBehavior> itsBehaviors = new HashMap<String, JBehavior>();
	private final Map<String, JField> itsFields = new HashMap<String, JField>();

	protected JClass(JInterpreter aInterpreter, JClass aSuperClass)
	{
		itsInterpreter = aInterpreter;
		itsSuperclass = aSuperClass;
	}
	
	public abstract String getName();
	
	@Override
	public JObject getInitialValue()
	{
		return null;
	}

	public static String getBehaviorKey(String aName, String aSignature)
	{
		return aName+"|"+aSignature;
	}

	public JClass getSuperclass()
	{
		return itsSuperclass;
	}
	
	public JInterpreter getInterpreter()
	{
		return itsInterpreter;
	}
	
	/**
	 * Creates a new uninitialized instance of this class. 
	 */
	public JInstance newInstance()
	{
		JInstance theInstance = new SimpleInstance(this);
		
		JClass theClass = this;
		while(theClass != null)
		{
			for (JField theField : theClass.getFields())
			{
				JObject theInitialValue = theField.getType().getInitialValue();
				theInstance.putFieldValue(theField, theInitialValue);
			}
			
			theClass = theClass.getSuperclass();
		}
		
		return theInstance;
	}
	
	protected void putBehavior(String aKey, JBehavior aBehavior)
	{
		itsBehaviors.put(aKey, aBehavior);
	}
	
	protected void putField(String aName, JField aField)
	{
		itsFields.put(aName, aField);
	}
	
	public JBehavior getBehavior(String aName, String aSignature)
	{
		return getBehavior(getBehaviorKey(aName, aSignature));
	}
	
	protected JBehavior getBehavior(String aKey)
	{
		return itsBehaviors.get(aKey);
	}
	
	public Iterable<JBehavior> getBehaviors()
	{
		return itsBehaviors.values();
	}
	
	public JField getField(String aName)
	{
		return itsFields.get(aName);
	}
	
	public Iterable<JField> getFields()
	{
		return itsFields.values();
	}

	public JBehavior getVirtualBehavior(String aMethodName, String aSignature)
	{
		JClass theClass = this;
		while(theClass != null) 
		{
			JBehavior theBehavior = theClass.getBehavior(aMethodName, aSignature);
			if (theBehavior != null) 
			{
				if (theBehavior.isPrivate()) return null;
				else return theBehavior;
			}
			theClass = theClass.getSuperclass();
		}
		return null;
	}
	
	public JField getVirtualField(String aName)
	{
		JClass theClass = this;
		while(theClass != null) 
		{
			JField theField = theClass.getField(aName);
			if (theField != null) 
			{
				if (theField.isPrivate()) return null;
				else return theField;
			}
			theClass = theClass.getSuperclass();
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
}
