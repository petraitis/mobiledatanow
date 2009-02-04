
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.gui;

// imports
import java.awt.Dimension;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * Superclass for all WSL application frames
 */
public class WslApplicationFrame extends JFrame implements WindowListener
{
    /**
     * Constructor taking params
     * @param title the text for the title bar of the application
     */
    public WslApplicationFrame(String title, Dimension d)
    {
        // init
        setTitle(title);
        setSize(d);
        GuiManager.centerWindow(this);

        // add window listener
        addWindowListener(this);
    }

    /**
     * Window has been closed by the user. Exit the application
     */
    public void windowClosing(WindowEvent e)
    {
        WslSwingApplication.exitApplication();
    }

    /**
     * Window listener overrides
     */
    public void windowOpened(WindowEvent e){}
    public void windowClosed(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}

}