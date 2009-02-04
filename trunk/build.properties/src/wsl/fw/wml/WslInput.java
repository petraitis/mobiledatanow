/**	$Id: WslInput.java,v 1.2 2002/07/18 03:12:15 jonc Exp $
 *
 * Extention of ecs.wml.Input to support default value and emptyok attributes.
 *
 */
package wsl.fw.wml;

import org.apache.ecs.wml.Input;
import org.apache.ecs.wml.Type;

public class WslInput extends Input
{
    /**
     * Support for superclass constructors.
     */
    public
	WslInput ()
    {
    }

    public
	WslInput (
	 String name)
    {
        super (name);
    }

    public
	WslInput (
	 String name,
	 Type type)
    {
        super (name, type);
    }

    public
	WslInput (
	 String format,
	 String name,
	 Type type)
    {
        super (format, name, type);
    }

    /**
     * Do-everything constructor, elements that are null are not set.
     */
    public
	WslInput (
	 String name,
	 String title,
	 String defaultValue,
	 Type type,
	 boolean emptyOk)
    {
        this ();
        if (name != null)
            setName (name);
        if (title != null)
            setTitle (title);
        setDefaultValue (defaultValue);
        if (type != null && !type.equals (Type.TEXT))
            setType (type);
        if (emptyOk)
            setEmptyOk (true);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the input emptyok attribute.
     */
    public WslInput
	setEmptyOk (
	 boolean emptyOk)
    {
        final String EMPTY_OK = "emptyok";

        if (emptyOk)
            addAttribute (EMPTY_OK, "true");
        else
            addAttribute (EMPTY_OK, "false");

        return this;
    }

    //--------------------------------------------------------------------------
    /**
     * Set the default attribute.
     */
    public WslInput
	setDefaultValue (
	 String defaultValue)
    {
        final String DEFAULT = "value";

        if (defaultValue != null)
            addAttribute (DEFAULT, defaultValue);

        return this;
    }
}
