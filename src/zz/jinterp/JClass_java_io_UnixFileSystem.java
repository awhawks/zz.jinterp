/*
 * Created on Dec 29, 2008
 */
package zz.jinterp;

import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JNormalBehavior.JFrame;

public class JClass_java_io_UnixFileSystem extends JNormalClass
{
	public static final String NAME = "java/io/UnixFileSystem";

	public JClass_java_io_UnixFileSystem(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, aNode);
		
		putBehavior("initIDs", "()V", 0, new Invocable()
		{
			@Override
			public JObject invoke(JFrame aParentFrame, JObject aTarget, JObject... aArgs)
			{
				// TODO: implement if necessary
				return JPrimitive.VOID;
			}
		});
	}

	
}
