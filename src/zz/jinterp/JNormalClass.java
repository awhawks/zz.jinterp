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

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class JNormalClass extends JClass
{
	private final ClassNode itsNode;

	protected JNormalClass(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, getSuperclass(aInterpreter, aNode), getInterfaces(aInterpreter, aNode));
		itsNode = aNode;
	}
	
	@Override
	void init()
	{
		if (itsNode != null)
		{
			for (Iterator theIterator = itsNode.methods.iterator(); theIterator.hasNext();)
			{
				MethodNode theMethodNode = (MethodNode) theIterator.next();
				if ((theMethodNode.access & Opcodes.ACC_NATIVE) != 0) continue;
				
				String theKey = getBehaviorKey(theMethodNode.name, theMethodNode.desc);
				if (getBehavior(theKey) != null) continue;
				putBehavior(theKey, new JNormalBehavior(this, theMethodNode));
			}
			
			for (Iterator theIterator = itsNode.fields.iterator(); theIterator.hasNext();)
			{
				FieldNode theFieldNode = (FieldNode) theIterator.next();
				JType theType = getInterpreter().getType(theFieldNode.desc);
				
				JField theField = (theFieldNode.access & Opcodes.ACC_STATIC) != 0 ?
						getInterpreter().createStaticField(this, theFieldNode.name, theType, theFieldNode.access)
						: new JField(this, theFieldNode.name, theType, theFieldNode.access);
				
				putField(theFieldNode.name, theField);
			}
		}
		
		super.init();
	}
	
	
	public static ClassNode readClass(byte[] aBytecode)
	{
		if (aBytecode == null) return null;
		ClassReader theReader = new ClassReader(aBytecode);
		ClassNode theNode = new ClassNode();
		theReader.accept(theNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		return theNode;
	}
	
	public static JClass getSuperclass(JInterpreter aInterpreter, ClassNode aClassNode)
	{
		JClass theClass = null;
		if (aClassNode != null) 
		{
			theClass = aClassNode.superName != null ? aInterpreter.getClass(aClassNode.superName) : null;
		}
		else
		{
			theClass = aInterpreter.getClass("java/lang/Object");
		}
		
		return theClass;
	}
	
	public static JClass[] getInterfaces(JInterpreter aInterpreter, ClassNode aClassNode)
	{
		if (aClassNode == null) return new JClass[0];
		
		JClass[] theResult = new JClass[aClassNode.interfaces.size()];
		for(int i=0;i<theResult.length;i++)
		{
			String theInterfaceName = (String) aClassNode.interfaces.get(i);
			JClass theInterface = aInterpreter.getClass(theInterfaceName);
			theResult[i] = theInterface;
		}
		return theResult;
	}
	
	@Override
	public String getName()
	{
		return itsNode != null ? itsNode.name : "invalid class";
	}
	
	@Override
	public boolean isInterface()
	{
		return (itsNode.access & Opcodes.ACC_INTERFACE) != 0;
	}
	
}
