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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import zz.jinterp.JPrimitive.JBitNumber;
import zz.jinterp.JPrimitive.JByte;
import zz.jinterp.JPrimitive.JChar;
import zz.jinterp.JPrimitive.JDouble;
import zz.jinterp.JPrimitive.JFloat;
import zz.jinterp.JPrimitive.JInt;
import zz.jinterp.JPrimitive.JLong;
import zz.jinterp.JPrimitive.JNumber;
import zz.jinterp.JPrimitive.JShort;
import zz.jinterp.SimpleInterp.SimpleArray;
import zz.utils.Utils;

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
	public JObject invoke0(JFrame aParentFrame, JObject aTarget, JObject... aArgs)
	{
		JObject[] theArgs;
		if ((getNode().access & Opcodes.ACC_STATIC) == 0) 
		{
			// Not static
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
			// Static
			if (aTarget != null) throw new RuntimeException("Cannot pass a target to a static method");
			theArgs = aArgs;
		}
		
		JFrame theFrame = new JFrame(aParentFrame, theArgs, getNode().maxLocals, getNode().maxStack);
		try
		{
			while (theFrame.step() != -1);
		}
		catch (ExceptionThrown e)
		{
			List<String> theStackTraceInfo = e.getException().getStackTraceInfo();
			StringBuilder theBuilder = new StringBuilder();
			for (String theEntry : theStackTraceInfo)
			{
				theBuilder.append(theEntry);
				theBuilder.append('\n');
			}
			throw new RuntimeException("Exception thrown during evaluation: "+e.getException().getType()+"\n"+theBuilder);
		}
		return theFrame.itsReturnValue;
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
		
		public JFrame(JFrame aParentFrame, JObject[] aArgs, int aNLocals, int aStackSize)
		{
			itsParentFrame = aParentFrame;
			itsLocals = new JObject[aNLocals];
			System.arraycopy(aArgs, 0, itsLocals, 0, aArgs.length);
			itsStack = new JObject[aStackSize];
			itsStackSize = 0;
			itsInstructionPointer = 0;
			itsReturnValue = JPrimitive.VOID;
		}
		
		public JFrame getParentFrame()
		{
			return itsParentFrame;
		}
		
		public int step()
		{
			AbstractInsnNode theInsnNode = getNode().instructions.get(itsInstructionPointer);
			try
			{
				theInsnNode.accept(this);
			}
			catch (ExceptionThrown e)
			{
				throwEx(e.getException());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throwEx(getInterpreter().new_Exception("RuntimeException", this, "JInterpreter exception: "+e.getMessage()));
			}
			return itsInstructionPointer;
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
		
		private boolean match(JInstance aException, String aType)
		{
			JClass theClass = aException.getType();
			while (theClass != null)
			{
				if (aType.equals(theClass.getName())) return true;
				theClass = theClass.getSuperclass();
			}
			return false;
		}

		private TryCatchBlock getHandler(JInstance aException)
		{
			for (TryCatchBlock theBlock : getTryCatchBlocks())
			{
				if (itsInstructionPointer < theBlock.start || itsInstructionPointer >= theBlock.end) continue;
				if (! match(aException, theBlock.type)) continue;
				return theBlock;
			}
			return null;
		}
		
		private void throwEx(JInstance aException)
		{
			// Add stack trace info
			List<String> theStackTraceInfo = aException.getStackTraceInfo();
			theStackTraceInfo.add(getDeclaringClass().getName()+"."+getName()+":"+itsInstructionPointer);
			
			// Handle exception
			TryCatchBlock theHandler = getHandler(aException);
			if (theHandler != null) 
			{
				itsStackSize = 0;
				push(aException);
				itsInstructionPointer = theHandler.handler;
			}
			else
			{
				throw new ExceptionThrown(aException);
			}
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
			if (theField == null) Utils.rtex("Cannot find field: %s, %s, %s", aOwner, aName, aDesc);

			switch(aOpcode)
			{
			case GETSTATIC:
				theClass.clInit(this);
				push(((JStaticField) theField).getStaticFieldValue());
				break;
				
			case PUTSTATIC: {
				theClass.clInit(this);
				JObject v = pop();
				((JStaticField) theField).putStaticFieldValue(v);
			} break;
				
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
			itsInstructionPointer++;
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
				push(array.get(index.v));
			} break;
				
			case IASTORE:
			case LASTORE:
			case FASTORE:
			case DASTORE:
			case AASTORE: {
				JObject value = pop();
				JInt index = (JInt) pop();
				JArray array = (JArray) pop();
				array.set(index.v, value);
			} break;
				
			case BASTORE: {
				JPrimitive value = (JPrimitive) pop();
				JInt index = (JInt) pop();
				JArray array = (JArray) pop();
				array.set(index.v, new JByte((byte) value.intValue()));
			} break;
			
			case CASTORE: {
				JPrimitive value = (JPrimitive) pop();
				JInt index = (JInt) pop();
				JArray array = (JArray) pop();
				array.set(index.v, new JChar((char) value.intValue()));
			} break;
			
			case SASTORE: {
				JPrimitive value = (JPrimitive) pop();
				JInt index = (JInt) pop();
				JArray array = (JArray) pop();
				array.set(index.v, new JShort((short) value.intValue()));
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
				push(new JInt(array.getSize()));
			} break;
				
			case ATHROW: {
				JInstance theException = (JInstance) pop();
				throwEx(theException);
			} return;
				
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
				push(new SimpleArray(size.v));
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
				JPrimitive x = (JPrimitive) pop();
				if (x.intValue() == 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFNE: {
				JPrimitive x = (JPrimitive) pop();
				if (x.intValue() != 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFLT: {
				JPrimitive x = (JPrimitive) pop();
				if (x.intValue() < 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFGE: {
				JPrimitive x = (JPrimitive) pop();
				if (x.intValue() >= 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFGT: {
				JPrimitive x = (JPrimitive) pop();
				if (x.intValue() > 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IFLE: {
				JPrimitive x = (JPrimitive) pop();
				if (x.intValue() <= 0) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPEQ: {
				JPrimitive x2 = (JPrimitive) pop();
				JPrimitive x1 = (JPrimitive) pop();
				if (x1.intValue() == x2.intValue()) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPNE: {
				JPrimitive x2 = (JPrimitive) pop();
				JPrimitive x1 = (JPrimitive) pop();
				if (x1.intValue() != x2.intValue()) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPLT: {
				JPrimitive x2 = (JPrimitive) pop();
				JPrimitive x1 = (JPrimitive) pop();
				if (x1.intValue() < x2.intValue()) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPGE: {
				JPrimitive x2 = (JPrimitive) pop();
				JPrimitive x1 = (JPrimitive) pop();
				if (x1.intValue() >= x2.intValue()) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPGT: {
				JPrimitive x2 = (JPrimitive) pop();
				JPrimitive x1 = (JPrimitive) pop();
				if (x1.intValue() > x2.intValue()) {
					jump(aLabel);
					return;
				}
			} break;
				
			case IF_ICMPLE: {
				JPrimitive x2 = (JPrimitive) pop();
				JPrimitive x1 = (JPrimitive) pop();
				if (x1.intValue() <= x2.intValue()) {
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
			JObject theResult;
			if (aCst instanceof Type)
			{
				Type theType = (Type) aCst;
				theResult = getInterpreter().getMetaclass(getInterpreter().getType(theType.getDescriptor()));
			}
			else
			{
				theResult = getInterpreter().toJObject(aCst);
			}
			push(theResult);
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
				if (theBehavior == null) Utils.rtex("Cannot find behavior: %s, %s, %s", aOwner, aName, aDesc);
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
				if (theBehavior == null) Utils.rtex("Behavior not found: %s %s in %s", aName, aDesc, aOwner);
				JObject[] theArgs = new JObject[theBehavior.getArgCount()];
				for(int i=theArgs.length-1;i>=0;i--) theArgs[i] = pop();
				JObject theTarget = pop();
				JObject theResult = theBehavior.invoke(this, theTarget, theArgs);
				if (theResult != JPrimitive.VOID) push(theResult);
			} break;
			
			case INVOKESTATIC: {
				JClass theClass = getInterpreter().getClass(aOwner);
				JBehavior theBehavior = theClass.getBehavior(JClass.getBehaviorKey(aName, aDesc));
				if (theBehavior == null) Utils.rtex("Behavior not found: %s %s in %s", aName, aDesc, theClass.getName());
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
				
			case ANEWARRAY:{
				JInt size = (JInt) pop();
				push(new SimpleArray(size.v));
			} break;
				
			case CHECKCAST: {
				JInstance o = (JInstance) pop();
				JClass theClass = getInterpreter().getClass(aType);
				boolean theResult = theClass.isAssignableFrom(o.getType());
				if (theResult) push(o);
				else throwEx(getInterpreter().new_ClassCastException(this, "Cannot cast "+o.getType()+" to "+aType));
			} break;
								
			case INSTANCEOF: {
				JInstance o = (JInstance) pop();
				JClass theClass = getInterpreter().getClass(aType);
				boolean theResult = theClass.isAssignableFrom(o.getType());
				push(new JInt(theResult ? 1 : 0));
			} break;
				
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

	private static class ExceptionThrown extends RuntimeException
	{
		private final JInstance itsException;

		public ExceptionThrown(JInstance aException)
		{
			itsException = aException;
		}
		
		public JInstance getException()
		{
			return itsException;
		}
	}
}
