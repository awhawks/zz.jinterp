/*
 * Created on Dec 29, 2008
 */
package zz.jinterp;

import java.security.PrivilegedAction;

import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JNormalBehavior.JFrame;

public class JClass_java_security_AccessController extends JNormalClass
{
	public static final String NAME = "java/security/AccessController";
	
	public JClass_java_security_AccessController(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, aNode);
		JReflectiveClass.initBehaviors(this, getClass());
	}

    public static Object __doPrivileged(PrivilegedAction action) { return null; }
	public JObject _doPrivileged(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		JInstance theAction = (JInstance) aArgs[0];
		JClass theClass = theAction.getType();
		JBehavior theBehavior = theClass.getBehavior(getBehaviorKey("run", "()Ljava/lang/Object;"));
		return theBehavior.invoke(aParentFrame, theAction);
	}

}
