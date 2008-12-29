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

public abstract class JPrimitive extends JObject
{
	public abstract int intValue();

	public static class JVoid extends JPrimitive
	{
		@Override
		public JType getType()
		{
			return JPrimitiveType.VOID;
		}

		@Override
		public int intValue()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	public static final JObject VOID = new JVoid();
	
	public static class JBoolean extends JPrimitive
	{
		public static final JBoolean _true = new JBoolean(true);
		public static final JBoolean _false = new JBoolean(false);
		
		public final boolean v;

		public JBoolean(boolean aV)
		{
			v = aV;
		}

		@Override
		public JType getType()
		{
			return JPrimitiveType.BOOLEAN;
		}

		@Override
		public int intValue()
		{
			return v ? 1 : 0;
		}
	}
	
	public static abstract class JNumber extends JPrimitive
	{
		public abstract JNumber add(JNumber aNumber);
		public abstract JNumber sub(JNumber aNumber);
		public abstract JNumber mul(JNumber aNumber);
		public abstract JNumber div(JNumber aNumber);
		public abstract JNumber rem(JNumber aNumber);
		public abstract JNumber neg();
	}
	
	public static abstract class JBitNumber extends JNumber
	{
		public abstract JBitNumber shl(int n);
		public abstract JBitNumber shr(int n);
		public abstract JBitNumber ushr(int n);
		
		public abstract JBitNumber and(JBitNumber aNumber);
		public abstract JBitNumber or(JBitNumber aNumber);
		public abstract JBitNumber xor(JBitNumber aNumber);
		public abstract JBitNumber not();
	}
	
	public static class JInt extends JBitNumber
	{
		public static final JInt _M1 = new JInt(-1);
		public static final JInt _0 = new JInt(0);
		public static final JInt _1 = new JInt(1);
		public static final JInt _2 = new JInt(2);
		public static final JInt _3 = new JInt(3);
		public static final JInt _4 = new JInt(4);
		public static final JInt _5 = new JInt(5);
		
		public final int v;
		
		public JInt(int aV)
		{
			v = aV;
		}

		@Override
		public JType getType()
		{
			return JPrimitiveType.INT;
		}

		@Override
		public JBitNumber and(JBitNumber aNumber)
		{
			return new JInt(v & ((JInt) aNumber).v);
		}

		@Override
		public JBitNumber not()
		{
			return new JInt(~v);
		}

		@Override
		public JBitNumber or(JBitNumber aNumber)
		{
			return new JInt(v | ((JInt) aNumber).v);
		}

		@Override
		public JBitNumber shl(int aN)
		{
			return new JInt(v << aN);
		}

		@Override
		public JBitNumber shr(int aN)
		{
			return new JInt(v >> aN);
		}

		@Override
		public JBitNumber ushr(int aN)
		{
			return new JInt(v >>> aN);
		}

		@Override
		public JBitNumber xor(JBitNumber aNumber)
		{
			return new JInt(v ^ ((JInt) aNumber).v);
		}

		@Override
		public JNumber add(JNumber aNumber)
		{
			return new JInt(v + ((JInt) aNumber).v);
		}

		@Override
		public JNumber div(JNumber aNumber)
		{
			return new JInt(v / ((JInt) aNumber).v);
		}

		@Override
		public JNumber mul(JNumber aNumber)
		{
			return new JInt(v * ((JInt) aNumber).v);
		}

		@Override
		public JNumber neg()
		{
			return new JInt(-v);
		}

		@Override
		public JNumber rem(JNumber aNumber)
		{
			return new JInt(v % ((JInt) aNumber).v);
		}

		@Override
		public JNumber sub(JNumber aNumber)
		{
			return new JInt(v - ((JInt) aNumber).v);
		}
		
		@Override
		public String toString()
		{
			return Integer.toString(v);
		}
		
		@Override
		public int intValue()
		{
			return v;
		}
	}
	
	public static class JLong extends JBitNumber
	{
		public static final JLong _0 = new JLong(0);
		public static final JLong _1 = new JLong(1);

		public final long v;
		
		public JLong(long aV)
		{
			v = aV;
		}

		@Override
		public JType getType()
		{
			return JPrimitiveType.INT;
		}

		@Override
		public JBitNumber and(JBitNumber aNumber)
		{
			return new JLong(v & ((JLong) aNumber).v);
		}

		@Override
		public JBitNumber not()
		{
			return new JLong(~v);
		}

		@Override
		public JBitNumber or(JBitNumber aNumber)
		{
			return new JLong(v | ((JLong) aNumber).v);
		}

		@Override
		public JBitNumber shl(int aN)
		{
			return new JLong(v << aN);
		}

		@Override
		public JBitNumber shr(int aN)
		{
			return new JLong(v >> aN);
		}

		@Override
		public JBitNumber ushr(int aN)
		{
			return new JLong(v >>> aN);
		}

		@Override
		public JBitNumber xor(JBitNumber aNumber)
		{
			return new JLong(v ^ ((JLong) aNumber).v);
		}

		@Override
		public JNumber add(JNumber aNumber)
		{
			return new JLong(v + ((JLong) aNumber).v);
		}

		@Override
		public JNumber div(JNumber aNumber)
		{
			return new JLong(v / ((JLong) aNumber).v);
		}

		@Override
		public JNumber mul(JNumber aNumber)
		{
			return new JLong(v * ((JLong) aNumber).v);
		}

		@Override
		public JNumber neg()
		{
			return new JLong(-v);
		}

		@Override
		public JNumber rem(JNumber aNumber)
		{
			return new JLong(v % ((JLong) aNumber).v);
		}

		@Override
		public JNumber sub(JNumber aNumber)
		{
			return new JLong(v - ((JLong) aNumber).v);
		}
		
		@Override
		public String toString()
		{
			return Long.toString(v);
		}
		
		@Override
		public int intValue()
		{
			throw new UnsupportedOperationException();
		}
	}

	public static class JFloat extends JNumber
	{
		public static final JFloat _0 = new JFloat(0);
		public static final JFloat _1 = new JFloat(1);
		public static final JFloat _2 = new JFloat(2);

		public final float v;
		
		public JFloat(float aV)
		{
			v = aV;
		}
		
		@Override
		public JType getType()
		{
			return JPrimitiveType.FLOAT;
		}

		@Override
		public JNumber add(JNumber aNumber)
		{
			return new JFloat(v + ((JFloat) aNumber).v);
		}

		@Override
		public JNumber div(JNumber aNumber)
		{
			return new JFloat(v / ((JFloat) aNumber).v);
		}

		@Override
		public JNumber mul(JNumber aNumber)
		{
			return new JFloat(v * ((JFloat) aNumber).v);
		}

		@Override
		public JNumber neg()
		{
			return new JFloat(-v);
		}

		@Override
		public JNumber rem(JNumber aNumber)
		{
			return new JFloat(v % ((JFloat) aNumber).v);
		}

		@Override
		public JNumber sub(JNumber aNumber)
		{
			return new JFloat(v - ((JFloat) aNumber).v);
		}
		
		@Override
		public String toString()
		{
			return Float.toString(v);
		}

		@Override
		public int intValue()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	public static class JDouble extends JNumber
	{
		public static final JDouble _0 = new JDouble(0);
		public static final JDouble _1 = new JDouble(1);

		public final double v;
		
		public JDouble(double aV)
		{
			v = aV;
		}
		
		@Override
		public JType getType()
		{
			return JPrimitiveType.DOUBLE;
		}

		@Override
		public JNumber add(JNumber aNumber)
		{
			return new JDouble(v + ((JDouble) aNumber).v);
		}

		@Override
		public JNumber div(JNumber aNumber)
		{
			return new JDouble(v / ((JDouble) aNumber).v);
		}

		@Override
		public JNumber mul(JNumber aNumber)
		{
			return new JDouble(v * ((JDouble) aNumber).v);
		}

		@Override
		public JNumber neg()
		{
			return new JDouble(-v);
		}

		@Override
		public JNumber rem(JNumber aNumber)
		{
			return new JDouble(v % ((JDouble) aNumber).v);
		}

		@Override
		public JNumber sub(JNumber aNumber)
		{
			return new JDouble(v - ((JDouble) aNumber).v);
		}
		
		@Override
		public String toString()
		{
			return Double.toString(v);
		}

		@Override
		public int intValue()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	public static class JByte extends JPrimitive
	{
		public static final JByte _0 = new JByte((byte) 0);
		public final byte v;
		
		public JByte(byte aV)
		{
			v = aV;
		}
		
		@Override
		public JType getType()
		{
			return JPrimitiveType.BYTE;
		}
		
		@Override
		public String toString()
		{
			return Byte.toString(v);
		}

		@Override
		public int intValue()
		{
			return v;
		}
	}
	
	public static class JChar extends JPrimitive
	{
		public static final JChar _0 = new JChar((char) 0);
		public final char v;
		
		public JChar(char aV)
		{
			v = aV;
		}
		
		@Override
		public JType getType()
		{
			return JPrimitiveType.CHAR;
		}
		
		@Override
		public String toString()
		{
			return Character.toString(v);
		}

		@Override
		public int intValue()
		{
			return v;
		}
	}
	
	public static class JShort extends JPrimitive
	{
		public static final JShort _0 = new JShort((short) 0);
		public final short v;
		
		public JShort(short aV)
		{
			v = aV;
		}
		
		@Override
		public JType getType()
		{
			return JPrimitiveType.SHORT;
		}
		
		@Override
		public String toString()
		{
			return Short.toString(v);
		}

		@Override
		public int intValue()
		{
			return v;
		}
	}
}
