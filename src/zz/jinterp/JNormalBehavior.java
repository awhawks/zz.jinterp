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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import zz.jinterp.JPrimitive.JBitNumber;
import zz.jinterp.JPrimitive.JByte;
import zz.jinterp.JPrimitive.JChar;
import zz.jinterp.JPrimitive.JDouble;
import zz.jinterp.JPrimitive.JFloat;
import zz.jinterp.JPrimitive.JInt;
import zz.jinterp.JPrimitive.JLong;
import zz.jinterp.JPrimitive.JNumber;
import zz.jinterp.JPrimitive.JShort;
import zz.jinterp.JPrimitive.JVoid;

public class JNormalBehavior extends JASMBehavior
{
	private final Map<Label, Integer> itsLabelToInsnMap = new HashMap<Label, Integer>();
	
	public JNormalBehavior(JClass aClass, MethodNode aMethodNode)
	{
		super(aClass, aMethodNode);
		Setup theSetup = new Setup();
		getNode().accept((MethodVisitor) theSetup);
	}
	
	@Override
	public JObject invoke(JFrame aParentFrame, JObject aTarget, JObject... aArgs)
	{
		JObject[] theArgs;
		if ((getNode().access & Opcodes.ACC_STATIC) == 0) 
		{
			if (aTarget == null) 
			{
				aParentFrame.throwEx(getInterpreter().new_NullPointerException(aParentFrame, "null"));
				return null;
			}
			theArgs = new JObject[aArgs.length+1];
			theArgs[0] = aTarget;
			System.arraycopy(aArgs, 0, theArgs, 1, aArgs.length);
		}
		else 
		{
			if (aTarget != null) throw new RuntimeException("Cannot pass a target to a static method");
			theArgs = aArgs;
		}
		
		JFrame theFrame = new JFrame(aParentFrame, theArgs, getNode().maxLocals, getNode().maxStack);
		while (theFrame.step() != -1);
		if (theFrame.itsException != null) return null;
		else return theFrame.itsReturnValue;
	}
	
	private class Setup extends EmptyVisitor
	{
		private int itsInstructionCounter = 0;

		@Override
		public void visitLabel(Label aLabel)
		{
			itsLabelToInsnMap.put(aLabel, itsInstructionCounter);
			itsInstructionCounter++;
		}
		
		@Override
		public void visitFieldInsn(int aOpcode, String aOwner, String aName, String aDesc)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitIincInsn(int aVar, int aIncrement)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitInsn(int aOpcode)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitIntInsn(int aOpcode, int aOperand)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitJumpInsn(int aOpcode, Label aLabel)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitLdcInsn(Object aCst)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitLookupSwitchInsn(Label aDflt, int[] aKeys, Label[] aLabels)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitMethodInsn(int aOpcode, String aOwner, String aName, String aDesc)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitMultiANewArrayInsn(String aDesc, int aDims)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitTableSwitchInsn(int aMin, int aMax, Label aDflt, Label[] aLabels)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitTypeInsn(int aOpcode, String aType)
		{
			itsInstructionCounter++;
		}

		@Override
		public void visitVarInsn(int aOpcode, int aVar)
		{
			itsInstructionCounter++;
		}
	}
	
	
	public class JFrame extends EmptyVisitor implements Opcodes
	{
		private final JFrame itsParentFrame;
		
		private JObject[] itsLocals;
		private JObject[] itsStack;
		private int itsStackSize;
		
		private int itsInstructionPointer;
		private JObject itsReturnValue;
		private JInstance itsException;
		
		public JFrame(JFrame aParentFrame, JObject[] aArgs, int aNLocals, int aStackSize)
		{
			itsParentFrame = aParentFrame;
			itsLocals = new JObject[aNLocals];
			System.arraycopy(aArgs, 0, itsLocals, 0, aArgs.length);
			itsStack = new JObject[aStackSize];
			itsStackSize = 0;
			itsInstructionPointer = 0;
			itsReturnValue = JPrimitive.VOID;
			itsException = null;
		}
		
		public JFrame getParentFrame()
		{
			return itsParentFrame;
		}
		
		public int step()
		{
			if (itsException != null)
			{
				if (itsParentFrame == null)
				{
					throw new RuntimeException("Exception: "+itsException);
				}
				itsParentFrame.throwEx(itsException);
				itsInstructionPointer = -1;
			}
			else
			{
				AbstractInsnNode theInsnNode = getNode().instructions.get(itsInstructionPointer);
				theInsnNode.accept(this);
			}
			return itsInstructionPointer;
		}
		
		public void throwEx(JInstance aException)
		{
			itsException = aException;
		}
		
		private void push(JObject aValue)
		{
			itsStack[itsStackSize++] = aValue;
		}
		
		private JObject pop()
		{
			return itsStack[--itsStackSize];
		}
		
		private JObject local(int aIndex)
		{
			return itsLocals[aIndex];
		}
		
		private void local(int aIndex, JObject aValue)
		{
			itsLocals[aIndex] = aValue;
		}

		@Override
		public void visitLabel(Label aLabel)
		{
			itsInstructionPointer++;
		}
		
		@Override
		public void visitFieldInsn(int aOpcode, String aOwner, String aName, String aDesc)
		{
			JClass theClass = getInterpreter().getClass(aOwner);
			JField theField = theClass.getVirtualField(aName);
			
			switch(aOpcode)
			{
			case GETSTATIC:
				throw new UnsupportedOperationException();
				
			case PUTSTATIC:
				throw new UnsupportedOperationException();
				
			case GETFIELD: {
				JInstance target = (JInstance) pop();
				JObject v = target.getFieldValue(theField);
				push(v);
			} break;
				
			case PUTFIELD: {
				JObject v = pop();
				JInstance target = (JInstance) pop();
				
				target.putFieldValue(theField, v);
			} break;
				
			default: 
				throw new UnsupportedOperationException();

			}
			itsInstructionPointer++;
		}

		@Override
		public void visitIincInsn(int aVar, int aIncrement)
		{
			JInt i = (JInt) local(aVar);
			local(aVar, new JInt(i.v+aIncrement));
		}

		@Override
		public void visitInsn(int aOpcode)
		{
			switch(aOpcode)
			{
			case NOP:
				break;
				
			case ACONST_NULL:
				push(null);
				break;
			
			case ICONST_M1:
				push(JInt._M1);
				break;
				
			case ICONST_0:
				push(JInt._0);
				break;
				
			case ICONST_1:
				push(JInt._1);
				break;
				
			case ICONST_2:
				push(JInt._2);
				break;
				
			case ICONST_3:
				push(JInt._3);
				break;
				
			case ICONST_4:
				push(JInt._4);
				break;
				
			case ICONST_5:
				push(JInt._5);
				break;
				
			case LCONST_0:
				push(new JLong(0));
				break;
				
			case LCONST_1:
				push(new JLong(1));
				break;
				
			case FCONST_0:
				push(JFloat._0);
				break;
				
			case FCONST_1:
				push(JFloat._1);
				break;
				
			case FCONST_2:
				push(JFloat._2);
				break;
				
			case DCONST_0:
				push(JDouble._0);
				break;
				
			case DCONST_1:
				push(JDouble._1);
				break;
				
			case IALOAD:
			case LALOAD:
			case FALOAD:
			case DALOAD:
			case AALOAD:
			case BALOAD:
			case CALOAD:
			case SALOAD: {
				JInt index = (JInt) pop();
				JArray array = (JArray) pop();
				push(array.v[index.v]);
			} break;
				
			case IASTORE:
			case LASTORE:
			case FASTORE:
			case DASTORE:
			case AASTORE:
			case BASTORE:
			case CASTORE:
			case SASTORE: {
				JInt index = (JInt) pop();
				JArray array = (JArray) pop();
				JObject value = pop();
				array.v[index.v] = value;
			} break;
				
			case POP:
				pop();
				break;
				
			case POP2:
				throw new UnsupportedOperationException();
				
			case DUP: {
				JObject o = pop();
				push(o);
				push(o);
			} break;
				
			case DUP_X1: {
				JObject o1 = pop();
				JObject o2 = pop();
				push(o1);
				push(o2);
				push(o1);
			} break;
				
			case DUP_X2:
				throw new UnsupportedOperationException();
				
			case DUP2:
				throw new UnsupportedOperationException();
				
			case DUP2_X1:
				throw new UnsupportedOperationException();
				
			case DUP2_X2:
				throw new UnsupportedOperationException();
				
			case SWAP: {
				JObject o1 = pop();
				JObject o2 = pop();
				push(o1);
				push(o2);
			} break;
				
			case IADD:
			case LADD:
			case FADD:
			case DADD: {
				JNumber n2 = (JNumber) pop();
				JNumber n1 = (JNumber) pop();
				push(n1.add(n2));
			} break;
				
			case ISUB:
			case LSUB:
			case FSUB:
			case DSUB: {
				JNumber n2 = (JNumber) pop();
				JNumber n1 = (JNumber) pop();
				push(n1.sub(n2));
			} break;
				
			case IMUL:
			case LMUL:
			case FMUL:
			case DMUL: {
				JNumber n2 = (JNumber) pop();
				JNumber n1 = (JNumber) pop();
				push(n1.mul(n2));
			} break;
				
			case IDIV:
			case LDIV:
			case FDIV:
			case DDIV: {
				JNumber n2 = (JNumber) pop();
				JNumber n1 = (JNumber) pop();
				push(n1.div(n2));
			} break;
				
			case IREM:
			case LREM:
			case FREM:
			case DREM: {
				JNumber n2 = (JNumber) pop();
				JNumber n1 = (JNumber) pop();
				push(n1.rem(n2));
			} break;
				
			case INEG:
			case LNEG:
			case FNEG:
			case DNEG: {
				JNumber n = (JNumber) pop();
				push(n.neg());
			} break;
				
			case ISHL:
			case LSHL: {
				JInt n2 = (JInt) pop();
				JBitNumber n1 = (JBitNumber) pop();
				push(n1.shl(n2.v));
			} break;
				
			case ISHR:
			case LSHR: {
				JInt n2 = (JInt) pop();
				JBitNumber n1 = (JBitNumber) pop();
				push(n1.shr(n2.v));
			} break;
				
			case IUSHR:
			case LUSHR: {
				JInt n2 = (JInt) pop();
				JBitNumber n1 = (JBitNumber) pop();
				push(n1.ushr(n2.v));
			} break;
				
			case IAND:
			case LAND: {
				JBitNumber n2 = (JBitNumber) pop();
				JBitNumber n1 = (JBitNumber) pop();
				push(n1.and(n2));
			} break;
				
			case IOR:
			case LOR: {
				JBitNumber n2 = (JBitNumber) pop();
				JBitNumber n1 = (JBitNumber) pop();
				push(n1.or(n2));
			} break;
				
			case IXOR:
			case LXOR: {
				JBitNumber n2 = (JBitNumber) pop();
				JBitNumber n1 = (JBitNumber) pop();
				push(n1.xor(n2));
			} break;
				
			case I2L: {
				JInt i = (JInt) pop();
				push(new JLong(i.v));
			} break;
				
			case I2F: {
				JInt i = (JInt) pop();
				push(new JFloat(i.v));
			} break;
				
			case I2D: {
				JInt i = (JInt) pop();
				push(new JDouble(i.v));
			} break;
				
			case L2I: {
				JLong i = (JLong) pop();
				push(new JInt((int) i.v));
			} break;
				
			case L2F: {
				JLong i = (JLong) pop();
				push(new JFloat(i.v));
			} break;
				
			case L2D: {
				JLong i = (JLong) pop();
				push(new JDouble(i.v));
			} break;
				
			case F2I: {
				JFloat i = (JFloat) pop();
				push(new JInt((int) i.v));
			} break;
				
			case F2L: {
				JFloat i = (JFloat) pop();
				push(new JLong((long) i.v));
			} break;
				
			case F2D: {
				JFloat i = (JFloat) pop();
				push(new JDouble(i.v));
			} break;
				
			case D2I: {
				JDouble i = (JDouble) pop();
				push(new JInt((int) i.v));
			} break;
				
			case D2L: {
				JDouble i = (JDouble) pop();
				push(new JLong((long) i.v));
			} break;
				
			case D2F: {
				JDouble i = (JDouble) pop();
				push(new JFloat((float) i.v));
			} break;
				
			case I2B: {
				JInt i = (JInt) pop();
				push(new JByte((byte) i.v));
			} break;
				
			case I2C: {
				JInt i = (JInt) pop();
				push(new JChar((char) i.v));
			} break;
				
			case I2S: {
				JInt i = (JInt) pop();
				push(new JShort((short) i.v));
			} break;
				
			case LCMP: {
				JLong l2 = (JLong) pop();
				JLong l1 = (JLong) pop();
				if (l1.v > l2.v) push(JInt._1);
				else if (l1.v == l2.v) push(JInt._0);
				else push(JInt._M1);
			} break;
				
			case FCMPL: {
				JFloat f2 = (JFloat) pop();
				JFloat f1 = (JFloat) pop();
				if (Float.isNaN(f1.v) || Float.isNaN(f2.v)) push(JInt._M1); 
				else if (f1.v > f2.v) push(JInt._1);
				else if (f1.v == f2.v) push(JInt._0);
				else push(JInt._M1);
			} break;
				
			case FCMPG: {
				JFloat f2 = (JFloat) pop();
				JFloat f1 = (JFloat) pop();
				if (Float.isNaN(f1.v) || Float.isNaN(f2.v)) push(JInt._1); 
				else if (f1.v > f2.v) push(JInt._1);
				else if (f1.v == f2.v) push(JInt._0);
				else push(JInt._M1);
			} break;
				
			case DCMPL: {
				JDouble d2 = (JDouble) pop();
				JDouble d1 = (JDouble) pop();
				if (Double.isNaN(d1.v) || Double.isNaN(d2.v)) push(JInt._M1); 
				else if (d1.v > d2.v) push(JInt._1);
				else if (d1.v == d2.v) push(JInt._0);
				else push(JInt._M1);
			} break;
				
			case DCMPG: {
				JDouble d2 = (JDouble) pop();
				JDouble d1 = (JDouble) pop();
				if (Double.isNaN(d1.v) || Double.isNaN(d2.v)) push(JInt._1); 
				else if (d1.v > d2.v) push(JInt._1);
				else if (d1.v == d2.v) push(JInt._0);
				else push(JInt._M1);
			} break;
				
			case IRETURN:
			case LRETURN:
			case FRETURN:
			case DRETURN:
			case ARETURN: 
				itsReturnValue = pop();
				itsInstructionPointer = -1;
				return;
				
			case RETURN:
				itsReturnValue = JPrimitive.VOID;
				itsInstructionPointer = -1;
				return;
				
			case ARRAYLENGTH: {
				JArray array = (JArray) pop();
				push(new JInt(array.v.length));
			} break;
				
			case ATHROW:
				throw new UnsupportedOperationException();
				
			case MONITORENTER:
				throw new UnsupportedOperationException();
				
			case MONITOREXIT:
				throw new UnsupportedOperationException();
				
			default:
				throw new UnsupportedOperationException();
			}
			
			itsInstructionPointer++;
		}

		@Override
		public void visitIntInsn(int aOpcode, int aOperand)
		{
			switch(aOpcode)
			{
			case BIPUSH:
			case SIPUSH:
				push(new JInt(aOperand));
				break;
				
			case NEWARRAY: {
				JInt size = (JInt) pop();
				push(new JArray(size.v));
			} break;
				
			default: 
				throw new UnsupportedOperationException();
			}
			
			itsInstructionPointer++;
		}
		
		private void jump(Label aLabel)
		{
			itsInstructionPointer = itsLabelToInsnMap.get(aLabel);
		}

		@Override
		public void visitJumpInsn(int aOpcode, Label aLabel)
		{
			switch(aOpcode)
			{
			case IFEQ: {
				JInt x = (JInt) pop();
				if (x.v == 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFNE: {
				JInt x = (JInt) pop();
				if (x.v != 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFLT: {
				JInt x = (JInt) pop();
				if (x.v < 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFGE: {
				JInt x = (JInt) pop();
				if (x.v >= 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFGT: {
				JInt x = (JInt) pop();
				if (x.v > 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFLE: {
				JInt x = (JInt) pop();
				if (x.v <= 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPEQ: {
				JInt x2 = (JInt) pop();
				JInt x1 = (JInt) pop();
				if (x1.v == x2.v) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPNE: {
				JInt x2 = (JInt) pop();
				JInt x1 = (JInt) pop();
				if (x1.v != x2.v) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPLT: {
				JInt x2 = (JInt) pop();
				JInt x1 = (JInt) pop();
				if (x1.v < x2.v) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPGE: {
				JInt x2 = (JInt) pop();
				JInt x1 = (JInt) pop();
				if (x1.v >= x2.v) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPGT: {
				JInt x2 = (JInt) pop();
				JInt x1 = (JInt) pop();
				if (x1.v > x2.v) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPLE: {
				JInt x2 = (JInt) pop();
				JInt x1 = (JInt) pop();
				if (x1.v <= x2.v) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ACMPEQ: {
				JObject o2 = pop();
				JObject o1 = pop();
				if (o1 == o2) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ACMPNE: {
				JObject o2 = pop();
				JObject o1 = pop();
				if (o1 != o2) {
					jump(aLabel);
					return;
				}
			} break;
				
			case GOTO:
				jump(aLabel);
				return;
				
			case JSR:
				throw new UnsupportedOperationException();
				
			case IFNULL: {
				JObject o = pop();
				if (o == null) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFNONNULL: {
				JObject o = pop();
				if (o != null) {
					jump(aLabel);
					return;
				}
			} break;
				
			default: 
				throw new UnsupportedOperationException();
			}
			itsInstructionPointer++;
		}

		@Override
		public void visitLdcInsn(Object aCst)
		{
			push(getInterpreter().toJObject(aCst));
			itsInstructionPointer++;
		}

		@Override
		public void visitLookupSwitchInsn(Label aDflt, int[] aKeys, Label[] aLabels)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitMethodInsn(int aOpcode, String aOwner, String aName, String aDesc)
		{
			switch(aOpcode)
			{
			case INVOKEVIRTUAL: 
			case INVOKEINTERFACE: {
				JBehavior theBehavior = getInterpreter().getVirtual(aOwner, aName, aDesc);
				JObject[] theArgs = new JObject[theBehavior.getArgCount()];
				for(int i=theArgs.length-1;i>=0;i--) theArgs[i] = pop();
				JInstance theTarget = (JInstance) pop();
				
				if (theTarget == null) 
				{
					throwEx(getInterpreter().new_NullPointerException(this, ""));
					return;
				}
				
				// Find the actual behavior
				theBehavior = theTarget.getType().getVirtualBehavior(aName, aDesc);
				
				// Invoke
				JObject theResult = theBehavior.invoke(this, theTarget, theArgs);
				if (theResult != JPrimitive.VOID) push(theResult);
			} break;
				
			case INVOKESPECIAL: {
				JClass theClass = getInterpreter().getClass(aOwner);
				JBehavior theBehavior = theClass.getBehavior(aName, aDesc);
				JObject[] theArgs = new JObject[theBehavior.getArgCount()];
				for(int i=theArgs.length-1;i>=0;i--) theArgs[i] = pop();
				JObject theTarget = pop();
				JObject theResult = theBehavior.invoke(this, theTarget, theArgs);
				if (theResult != JPrimitive.VOID) push(theResult);
			} break;
			
			case INVOKESTATIC: {
				JBehavior theBehavior = getInterpreter().getVirtual(aOwner, aName, aDesc);
				JObject[] theArgs = new JObject[theBehavior.getArgCount()];
				for(int i=theArgs.length-1;i>=0;i--) theArgs[i] = pop();
				JObject theResult = theBehavior.invoke(this, null, theArgs);
				if (theResult != JPrimitive.VOID) push(theResult);
			} break;
				
			default: 
				throw new UnsupportedOperationException();

			}
			itsInstructionPointer++;
		}

		@Override
		public void visitMultiANewArrayInsn(String aDesc, int aDims)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitTableSwitchInsn(int aMin, int aMax, Label aDflt, Label[] aLabels)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitTypeInsn(int aOpcode, String aType)
		{
			switch(aOpcode)
			{
			case NEW: {
				JClass theClass = getInterpreter().getClass(aType);
				push(theClass.newInstance());
			} break;
				
			case ANEWARRAY:
				throw new UnsupportedOperationException();
				
			case CHECKCAST:
				throw new UnsupportedOperationException();
								
			case INSTANCEOF:
				throw new UnsupportedOperationException();
				
			default: 
				throw new UnsupportedOperationException();

			}
			itsInstructionPointer++;
		}

		@Override
		public void visitVarInsn(int aOpcode, int aVar)
		{
			switch(aOpcode)
			{
			case ILOAD:
			case LLOAD:
			case FLOAD:
			case DLOAD:
			case ALOAD:
				push(local(aVar));
				break;
				
			case ISTORE:
			case LSTORE:
			case FSTORE:
			case DSTORE:
			case ASTORE:
				local(aVar, pop());
				break;
				
			case RET:
				throw new UnsupportedOperationException();
				
			default: 
				throw new UnsupportedOperationException();
			}
			itsInstructionPointer++;
		}
	}

}
