/*
 * Created on Dec 25, 2008
 */
package zz.jinterp;

public abstract class JStaticField extends JField
{
	public JStaticField(JClass aClass, String aName, JType aType, int aAccess)
	{
		super(aClass, aName, aType, aAccess);
	}
	
	public abstract JObject getStaticFieldValue();
	public abstract void putStaticFieldValue(JObject aValue);
}
