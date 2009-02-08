/*
 * Created on Feb 7, 2009
 */
package zz.jinterp;

import java.lang.reflect.Constructor;

import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JNormalBehavior.JFrame;

public class JClass_sun_reflect_ReflectionFactory extends JNormalClass
{
	public static final String NAME = "sun/reflect/ReflectionFactory";

	public JClass_sun_reflect_ReflectionFactory(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, aNode);
		JReflectiveClass.initBehaviors(this, getClass());
		JReflectiveClass.addDefaultCtor(this);
	}
	
    public static Constructor __newConstructorForSerialization(Class aClass, Constructor aConstructor) { return null; }
	public JObject _newConstructorForSerialization(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		throw new UnsupportedOperationException();
	}

}
