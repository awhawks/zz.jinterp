/*
 * Created on Feb 7, 2009
 */
package zz.jinterp;

import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JNormalBehavior.JFrame;

public class JClass_sun_reflect_ReflectionFactory$GetReflectionFactoryAction extends JNormalClass
{
	public static final String NAME = "sun/reflect/ReflectionFactory$GetReflectionFactoryAction";

	public JClass_sun_reflect_ReflectionFactory$GetReflectionFactoryAction(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, aNode);
		JReflectiveClass.initBehaviors(this, getClass());
		JReflectiveClass.addDefaultCtor(this);
	}
	
    public static Object __run() { return null; }
	public JObject _run(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		return aTarget.getType().getInterpreter().getReflectionFactory(aParentFrame);
	}

}
