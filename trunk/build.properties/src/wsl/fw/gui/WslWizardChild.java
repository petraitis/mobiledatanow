//==============================================================================
// WslWizardChild.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import wsl.fw.help.HelpId;

//------------------------------------------------------------------------------
/**
 * Class than CAN be hosted in a WslWizardPanel.
 *
 * If the controlling wizard needs to alter the next/prev/finish behaviour
 * it can do so by setting the _can... flags as desired when it constructs
 * the child panel.
 */
public class WslWizardChild extends WslPanel
{
    public    boolean        _canNext      = true;
    public    boolean        _canPrev      = true;
    public    boolean        _canFinish    = false;

    protected WslWizardPanel _wizardParent = null;

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public WslWizardChild()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Set the wizard parent.
     * Called after construction when the panel is about to be displayed in a
     * wizard.
     * May be overridden if the subclass needs to alter its behaviour depending
     * on whether it is in a wizard or not.
     */
    public void setWizardParent(WslWizardPanel wp)
    {
        _wizardParent = wp;
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if this panel is displayed in a wizard. Will be false at
     *   construction time and may become true if the panel is later displayed
     *   in a wizard.
     */
    public boolean isInWizard()
    {
        return _wizardParent != null;
    }

    //--------------------------------------------------------------------------
    /**
     * If the subclass has help override this to specify the HelpId.
     * This help is displayed using the parent wizards's help button.
     * @return the HelpId of the help to display, if null the help button is not
     *   displayed.
     */
    public HelpId getHelpId()
    {
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Override if the subclass needs to display some title text when in a
     * wizard.
     * @return text that will be displayed above the wizard child panel,
     *   may be null.
     */
    public String getWizardText()
    {
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if the next button is to be enabled.
     */
    public boolean canNext()
    {
        return _canNext;
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if the prev button is to be enabled.
     */
    public boolean canPrev()
    {
        return _canPrev;
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if the finish button is to be enabled.
     */
    public boolean canFinish()
    {
        return _canFinish;
    }
}

//==============================================================================
// end of file WizardChild.java
//==============================================================================
