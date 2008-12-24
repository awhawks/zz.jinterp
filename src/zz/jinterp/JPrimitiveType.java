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

public abstract class JPrimitiveType extends JType
{
	public static final JVoid VOID = new JVoid();
	public static class JVoid extends JPrimitiveType
	{
		@Override
		public JObject getInitialValue()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	public static final JInt INT = new JInt();
	public static class JInt extends JPrimitiveType
	{
		@Override
		public JObject getInitialValue()
		{
			return JPrimitive.JInt._0;
		}
	}
	
	public static final JLong LONG = new JLong();
	public static class JLong extends JPrimitiveType
	{
		@Override
		public JObject getInitialValue()
		{
			return JPrimitive.JLong._0;
		}
	}
	
	public static final JFloat FLOAT = new JFloat();
	public static class JFloat extends JPrimitiveType
	{
		@Override
		public JObject getInitialValue()
		{
			return JPrimitive.JFloat._0;
		}
	}
	
	public static final JDouble DOUBLE = new JDouble();
	public static class JDouble extends JPrimitiveType
	{
		@Override
		public JObject getInitialValue()
		{
			return JPrimitive.JDouble._0;
		}
	}
	
	public static final JByte BYTE = new JByte();
	public static class JByte extends JPrimitiveType
	{
		@Override
		public JObject getInitialValue()
		{
			return JPrimitive.JByte._0;
		}
	}
	
	public static final JChar CHAR = new JChar();
	public static class JChar extends JPrimitiveType
	{
		@Override
		public JObject getInitialValue()
		{
			return JPrimitive.JChar._0;
		}
	}
	
	public static final JShort SHORT = new JShort();
	public static class JShort extends JPrimitiveType
	{
		@Override
		public JObject getInitialValue()
		{
			return JPrimitive.JShort._0;
		}
	}
	
	public static final JBoolean BOOLEAN = new JBoolean();
	public static class JBoolean extends JPrimitiveType
	{
		@Override
		public JObject getInitialValue()
		{
			return JPrimitive.JInt._0;
		}
	}
}
