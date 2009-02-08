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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import zz.jinterp.JClass.Invocable;
import zz.jinterp.JNormalBehavior.JFrame;

public abstract class JReflectiveClass extends JClass
{
	public JReflectiveClass(JInterpreter aInterpreter, JClass aSuperClass, JClass[] aInterfaces)
	{
		super(aInterpreter, aSuperClass, aInterfaces);
		initBehaviors(this, getClass());
	}
	
	@Override
	public boolean isInterface()
	{
		return false;
	}

	public static void addDefaultCtor(JClass aTarget)
	{
		aTarget.putBehavior("<init>", "()V", 0, new Invocable()
		{
			@Override
			public JObject invoke(JFrame aParentFrame, JObject aTarget, JObject... aArgs)
			{
				return JPrimitive.VOID;
			}
		});		
	}
	
	/**
	 * Adds JBehaviors to the target class, taking them from the specified java class.
	 */
	public static void initBehaviors(JClass aTarget, Class aClass)
	{
		try
		{
			for (Method theSigMethod : aClass.getDeclaredMethods())
			{
				if (! theSigMethod.getName().startsWith("__")) continue;
				
				Method theExecMethod = aClass.getMethod(
						theSigMethod.getName().substring(1), 
						JFrame.class, JInstance.class, JObject[].class);
				
				String theKey = getBehaviorKey(
						theSigMethod.getName().substring(2), 
						getSignature(theSigMethod.getParameterTypes(), theSigMethod.getReturnType()));
				
				aTarget.putBehavior(
						theKey, 
						new JReflectiveBehavior(aTarget, theExecMethod, theSigMethod.getParameterTypes().length));
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static String getSignature(Class[] aParams, Class aReturn)
	{
		StringBuilder theBuilder = new StringBuilder();
		theBuilder.append("(");
		for (Class theClass : aParams) theBuilder.append(getSignature(theClass));
		theBuilder.append(")");
		theBuilder.append(getSignature(aReturn));
		return theBuilder.toString();
	}
	
	private static String getSignature(Class aClass)
	{
		if (aClass.isPrimitive())
		{
			if (aClass == void.class) return "V";
			else if (aClass == boolean.class) return "Z";
			else if (aClass == char.class) return "C";
			else if (aClass == byte.class) return "B";
			else if (aClass == short.class) return "S";
			else if (aClass == int.class) return "I";
			else if (aClass == float.class) return "F";
			else if (aClass == long.class) return "J";
			else if (aClass == double.class) return "D";
			else throw new RuntimeException("Not handled: "+aClass);
		}
		else if (aClass.isArray()) return aClass.getName().replace('.', '/');
		else return "L"+aClass.getName().replace('.', '/')+";";
	}
	
	private static class JReflectiveBehavior extends JBehavior
	{
		private final Method itsMethod;
		private final int itsArgCount;
		
		public JReflectiveBehavior(JClass aClass, Method aMethod, int aArgCount)
		{
			super(aClass);
			itsMethod = aMethod;
			itsArgCount = aArgCount;
		}

		@Override
		public boolean isPrivate()
		{
			return Modifier.isPrivate(itsMethod.getModifiers());
		}
		
		@Override
		public int getArgCount()
		{
			return itsArgCount;
		}

		@Override
		public JObject invoke0(JFrame aParentFrame, JObject aTarget, JObject... aArgs)
		{
			Object[] theArgs = {aParentFrame, aTarget, aArgs};
			try
			{
				return (JObject) itsMethod.invoke(getDeclaringClass(), theArgs);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public String getName()
		{
			return itsMethod.getName();
		}
	}
	
}
