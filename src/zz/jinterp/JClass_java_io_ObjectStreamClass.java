/*
 * Created on Feb 7, 2009
 */
package zz.jinterp;

import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JNormalBehavior.JFrame;

public class JClass_java_io_ObjectStreamClass extends JNormalClass
{
	public static final String NAME = "java/io/ObjectStreamClass";
	
	public JClass_java_io_ObjectStreamClass(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, aNode);
		JReflectiveClass.initBehaviors(this, getClass());
	}
	
    public static void __initNative() { }
	public JObject _initNative(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		return JPrimitive.VOID;
	}

}
