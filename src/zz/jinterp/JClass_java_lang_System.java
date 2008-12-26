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

import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JNormalBehavior.JFrame;
import zz.jinterp.JPrimitive.JInt;
import zz.jinterp.JPrimitive.JLong;


public class JClass_java_lang_System extends JNormalClass
{
	public static final String NAME = "java/lang/System";
	
	public JClass_java_lang_System(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, aNode);
		JReflectiveClass.initBehaviors(this, getClass());
	}
	
	public static void __arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {}
	public JObject _arraycopy(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		JArray src = (JArray) aArgs[0];
		JInt srcPos = (JInt) aArgs[1];
		JArray dest = (JArray) aArgs[2];
		JInt destPos = (JInt) aArgs[3];
		JInt len = (JInt) aArgs[4];
		
		for(int i=0;i<len.v;i++) dest.set(i+destPos.v, src.get(i+srcPos.v));
		
		return JPrimitive.VOID;
	}

	public static void __registerNatives() {}
	public JObject _registerNatives(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		return JPrimitive.VOID;
	}
	
    public static long __currentTimeMillis() { return 0; };
	public JObject _currentTimeMillis(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		return new JLong(System.currentTimeMillis());
	}

}
