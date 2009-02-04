package wsl.JLinkLabelLibrary;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class JLinkAction extends AbstractAction
{
	public boolean actionCalled = false;
	public String actionCommand = "";
	
	public void actionPerformed(final ActionEvent e)
	{
	    actionCalled = true;
	    actionCommand = e.getActionCommand();
	}
}
