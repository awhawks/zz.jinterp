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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import zz.jinterp.JNormalBehavior.JFrame;
import zz.utils.Utils;

public class SimpleInterp extends JInterpreter
{
	private JInstance itsReflectionFactory;
	
	@Override
	protected byte[] getClassBytecode(String aName)
	{
		try
		{
			InputStream theStream = getClass().getResourceAsStream("/"+aName+".class");
			if (theStream == null) return null;
			else return Utils.readInputStream_byte(theStream);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public JStaticField createStaticField(JClass aClass, String aName, JType aType, int aAccess)
	{
		return new SimpleStaticField(aClass, aName, aType, aAccess);
	}

	@Override
	public JInstance getFileSystem(JFrame aParentFrame)
	{
		try
		{
			Field theField = File.class.getDeclaredField("fs");
			theField.setAccessible(true);
			Object theFileSystem = theField.get(null);
			
			return instantiate(aParentFrame, theFileSystem.getClass().getName().replace('.', '/'), "()V");
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public JInstance getReflectionFactory(JFrame aParentFrame)
	{
		if (itsReflectionFactory == null)
		{
			itsReflectionFactory = instantiate(aParentFrame, JClass_sun_reflect_ReflectionFactory.NAME, "()V");
		}
		
		return itsReflectionFactory;
	}
	
	public static class SimpleInstance extends JInstance
	{
		private final Map<JField, JObject> itsFields = new HashMap<JField, JObject>();
		
		public SimpleInstance(JClass aClass)
		{
			super(aClass);
		}
		
		@Override
		public JObject getFieldValue(JField aField)
		{
			return itsFields.get(aField);
		}

		@Override
		public void putFieldValue(JField aField, JObject aValue)
		{
			itsFields.put(aField, aValue);
		}
	}
	
	public static class SimpleArray extends JArray
	{
		private final JObject[] itsValues;
		
		public SimpleArray(int aSize)
		{
			itsValues = new JObject[aSize];
		}

		@Override
		public int getSize()
		{
			return itsValues.length;
		}

		@Override
		public JObject get(int aIndex)
		{
			return itsValues[aIndex];
		}

		@Override
		public void set(int aIndex, JObject aValue)
		{
			itsValues[aIndex] = aValue;
		}
	}
	
	public static class SimpleStaticField extends JStaticField
	{
		private JObject itsStaticValue;
		
		public SimpleStaticField(JClass aClass, String aName, JType aType, int aAccess)
		{
			super(aClass, aName, aType, aAccess);
			itsStaticValue = getType().getInitialValue();
		}
		
		public SimpleStaticField(JClass aClass, String aName, JType aType, int aAccess, JObject aStaticValue)
		{
			super(aClass, aName, aType, aAccess);
			itsStaticValue = aStaticValue;
		}

		@Override
		public JObject getStaticFieldValue()
		{
			return itsStaticValue;
		}
		
		@Override
		public void putStaticFieldValue(JObject aValue)
		{
			itsStaticValue = aValue;
		}
	}
}
