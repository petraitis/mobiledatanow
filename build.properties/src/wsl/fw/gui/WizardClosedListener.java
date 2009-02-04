//==============================================================================
// WizardClosedListener.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

/**
 * Interface for classes that need to be notified when a wizard closes.
 */
public interface WizardClosedListener
{
    /**
     * Function called to notify of the close event.
     * @param bFinished, if true the wizard was closed by pressing the finish
     *   button, if fals it was closed by the cancel button.
     */
    public void wizardClosed(boolean bFinished);
}

//==============================================================================
// end of file WizardClosedListener.java
//==============================================================================
