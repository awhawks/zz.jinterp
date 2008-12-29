/*
 * Created on Dec 29, 2008
 */
package zz.jinterp;

import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JNormalBehavior.JFrame;

public class JClass_java_lang_Throwable extends JNormalClass
{
	public static final String NAME = "java/lang/Throwable";
	
	public JClass_java_lang_Throwable(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, aNode);
		JReflectiveClass.initBehaviors(this, getClass());
	}

    public synchronized Throwable __fillInStackTrace() { return null; }
	public JObject _fillInStackTrace(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		// TODO: implement (if needed)
		return aTarget;
	}

}
