package wsl.JLinkLabelLibrary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JLinkLabelActionListener implements ActionListener
{
	public boolean actionCalled = false;
	public String actionCommand = "";
	
	public void actionPerformed(final ActionEvent e)
	{
	    actionCalled = true;
	    actionCommand = e.getActionCommand();
	}
}
