package wsl.fw.gui;

// imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JDialog;
import wsl.fw.util.Util;
import wsl.fw.util.WslTimer;
import wsl.fw.util.WslTimerListener;
import wsl.fw.gui.WslLabel;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class WslProgressPanel extends WslWizardChild
    implements Runnable, WslTimerListener
{
    //--------------------------------------------------------------------------
    // constants

    private static final int TIMER_DELAY = 100;
    public static final int UNKNOWN_PROGRESS = -1;


    //--------------------------------------------------------------------------
    // attributes

    private WslProgressClient _client;
    private WslTimer _timer;


    //--------------------------------------------------------------------------
    // controls

    private WslLabel _lblMessage = new WslLabel("");


    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor taking a client
     * @param client the progress client
     */
    public WslProgressPanel(WslProgressClient client)
    {
        // set client
        _client = client;

        // controls
        initProgressPanelControls();
    }

    /**
     * Init the panel controls
     */
    private void initProgressPanelControls()
    {
        // this
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // message
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(_lblMessage, gbc);
    }


    //--------------------------------------------------------------------------
    // run

    /**
     * Static method to run a progress panel in its own thread
     * @param panel the panel to run
     */
    public static void runProgressPanel(WslProgressPanel panel)
    {
        // create a thread
        Thread thread = new Thread(panel);
        thread.start();
    }

    /**
     * Run the panel
     */
    public void run()
    {
        // show self
        ((JDialog)this.getFrameParent()).show();

        // create and start the timer
        _timer = new WslTimer(TIMER_DELAY, this);
        _timer.start();

        // start the client
        Thread thread = new Thread(_client);
        thread.start();
    }

    /**
     * Timer notification
     */
    public void onTimer(WslTimer timer)
    {
        // must have a client
        Util.argCheckNull(_client);

        // is the client finished
        if(_client.isFinished())
        {
            // stop the timer
            _timer.stop();
            _timer = null;

            // if we have an error show it
            String strError = _client.getError();
            if(strError != null && strError.length() > 0)
                GuiManager.showErrorDialog(this.getFrameParent(), strError, null);

            // close self
            this.closePanel();
        }

        // poll the client for a new message
        else
        {
            _lblMessage.setText(_client.getProgressMessage());
        }
    }


    //--------------------------------------------------------------------------

    /**
     * @return Dimension preferred size of panel
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(400, 80);
    }
}