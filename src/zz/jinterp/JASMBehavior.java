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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

public abstract class JASMBehavior extends JBehavior
{
	private final MethodNode itsMethodNode;
	private final int itsArgCount;
	private final List<TryCatchBlock> itsTryCatchBlocks = new ArrayList<TryCatchBlock>();
	private final Map<LabelNode, Integer> itsLabelToIP = new HashMap<LabelNode, Integer>();

	public JASMBehavior(JClass aClass, MethodNode aMethodNode)
	{
		super(aClass);
		itsMethodNode = aMethodNode;
		Type[] theArgumentTypes = Type.getArgumentTypes(itsMethodNode.desc);
		itsArgCount = theArgumentTypes.length;
		

		// Map labels to instruction pointers
		for(int i=0;i<getNode().instructions.size();i++)
		{
			AbstractInsnNode theInsn = getNode().instructions.get(i);
			if (theInsn instanceof LabelNode) itsLabelToIP.put((LabelNode) theInsn, i);
		}

		// Transform try-catch blocks
		for (Iterator theIterator = getNode().tryCatchBlocks.iterator(); theIterator.hasNext();)
		{
			TryCatchBlockNode theBlock = (TryCatchBlockNode) theIterator.next();
			itsTryCatchBlocks.add(new TryCatchBlock(
					getLabelIP(theBlock.start),
					getLabelIP(theBlock.end),
					getLabelIP(theBlock.handler),
					theBlock.type));
		}
	}
	
	protected int getLabelIP(LabelNode aNode)
	{
		return itsLabelToIP.get(aNode);
	}
	
	public List<TryCatchBlock> getTryCatchBlocks()
	{
		return itsTryCatchBlocks;
	}
	
	public MethodNode getNode()
	{
		return itsMethodNode;
	}
	
	@Override
	public String getName()
	{
		return getNode().name;
	}
	
	@Override
	public boolean isPrivate()
	{
		return (itsMethodNode.access & Opcodes.ACC_PRIVATE) != 0;
	}
	
	@Override
	public int getArgCount()
	{
		return itsArgCount;
	}

	/**
	 * Same as {@link TryCatchBlockNode} but with intruction pointers instead of labels.
	 * @author gpothier
	 */
	public static final class TryCatchBlock
	{
		public final int start;
		public final int end;
		public final int handler;
		public final String type;
		
		public TryCatchBlock(int aStart, int aEnd, int aHandler, String aType)
		{
			start = aStart;
			end = aEnd;
			handler = aHandler;
			type = aType;
		}
	}
}
