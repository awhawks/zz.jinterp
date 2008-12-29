/*
 * Created on Dec 29, 2008
 */
package zz.jinterp;

import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JNormalBehavior.JFrame;

public class JClass_java_io_FileSystem extends JNormalClass
{
	public static final String NAME = "java/io/FileSystem";

	public JClass_java_io_FileSystem(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, aNode);
		
		putBehavior("getFileSystem", "()Ljava/io/FileSystem;", 0, new Invocable()
		{
			@Override
			public JObject invoke(JFrame aParentFrame, JObject aTarget, JObject... aArgs)
			{
				return getInterpreter().getFileSystem(aParentFrame);
			}
		});
	}

	
}
