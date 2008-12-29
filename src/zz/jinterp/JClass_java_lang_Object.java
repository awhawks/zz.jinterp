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
import zz.jinterp.JPrimitive.JInt;


public class JClass_java_lang_Object extends JReflectiveClass
{
	public static final String NAME = "java/lang/Object";
	
	public JClass_java_lang_Object(JInterpreter aInterpreter)
	{
		super(aInterpreter, null, new JClass[0]);
		
		putBehavior("<init>", "()V", 0, new Invocable()
		{
			@Override
			public JObject invoke(JFrame aParentFrame, JObject aTarget, JObject... aArgs)
			{
				return JPrimitive.VOID;
			}
		});
	}

	@Override
	public String getName()
	{
		return NAME;
	}
	
	private static void __registerNatives() {}
	public JObject _registerNatives(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		return JPrimitive.VOID;
	}
	

	public Class __getClass() {return null;}
	public JObject _getClass(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		return getInterpreter().getMetaclass(aTarget.getType());
	}
	
	public String __toString() {return null;}
	public JObject _toString(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		JClass theClass = aTarget.getType();
		JInt theCode = (JInt) theClass.getVirtualBehavior("hashCode", "()I").invoke(aParentFrame, aTarget);
		String theString = theClass.getName().replace('/', '.') + "@" + Integer.toHexString(theCode.v);
		return theClass.getInterpreter().toJString(theString);
	}
	
	public int __hashCode() {return 0;}
	public JObject _hashCode(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		int theCode = System.identityHashCode(aTarget);
		return new JInt(theCode);
	}
}
