package wsl.JLinkLabelLibrary;
/* JLinkLabel.java
 * Created on March 3, 2007, 4:55 AM
 */
 
/*

 Copyright [2007] [Timothy Binkley-Jones]
 
 Licensed under the Apache License, Version 2.0 (the "License"); 
 you may not use this file except in compliance with the License. 
 You may obtain a copy of the License at 
    
    http://www.apache.org/licenses/LICENSE-2.0 
 
 Unless required by applicable law or agreed to in writing, software 
 distributed under the License is distributed on an "AS IS" BASIS, 
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 See the License for the specific language governing permissions and 
 limitations under the License. 

 */

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.InvalidParameterException;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;

/** A link label control.
 * @author Tim
 */
public class JLinkLabel extends JLabel implements MouseListener
{
    /** Creates a new instance of LinkLabel. */
    public JLinkLabel()
    {
	addMouseListener(this);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" link properties ">
    
    /** constant to be used in property change events and action properties. */
    public static final String START = "start";
    /** constant to be used in property change events and action properties. */
    public static final String LENGTH = "length";
    /** constant to be used in property change events and action properties. */
    public static final String VISITED = "visted";
    /** constant to be used in property change events and action properties. */
    public static final String LINKCOLOR = "linkColor";
    /** constant to be used in property change events and action properties. */
    public static final String VISITEDLINKCOLOR = "visitedLinkColor";
    /** constant to be used in property change events and action properties. */
    public static final String LINKBEHAVIOR = "linkBehavior";
    
    /** Gets the starting position of the link within the text of the JLinkLabel,
     * -1 if the entire text will be a link.
     * @return the starting position of the link
     */
    public int getStart()
    {
	return startPosition;
    }
    
    /** Sets the starting position of the link within the text of the JLinkLabel.
     * @param value the starting position of the link
     */
    public void setStart(final int value)
    {
	if(value < 0) throw new InvalidParameterException("start position cannot be less than zero.");
	int oldValue = startPosition;
	startPosition = value;
	buildLabelText();
	firePropertyChange(START, oldValue, startPosition);
    }
    
    /** internal data for property. */
    private int startPosition = 0;
    
    
    /** Gets the length of the link within the text of the JLinkLabel,
     * -1 if the entire text will be a link.
     * @return the length of the link text
     */
    public int getLength()
    {
	int returnValue = linkLength;
	if(returnValue == 0)
	{
	    returnValue = unmodifiedText.length();
	}
	return returnValue;
    }
    
    /** Sets the length of the link within the text of the JLinkLabel,
     * -1 if the entire text will be a link.
     * @param value the length of the link
     */
    public void setLength(final int value)
    {
	if(value < 0) throw new InvalidParameterException("length cannot be less than zero.");
	int oldValue = linkLength;
	linkLength = value;
	buildLabelText();
	firePropertyChange(LENGTH, oldValue, linkLength);
    }
    
    /** internal data for property. */
    private int linkLength = 0;
    
    
    /** Gets a flag indicating whether the user has visited the link.
     * @return true if the user has visited the link
     */
    public boolean isVisited()
    {
	return linkVisited;
    }
    
    /** Sets a flag indicating whether the user has visited the link.
     * @param value true if the user has visited the link
     */
    public void setVisited(final boolean value)
    {
	boolean oldValue = linkVisited;
	linkVisited = value;
	buildLabelText();
	firePropertyChange(VISITED, oldValue, linkVisited);
    }
    
    /** internal data for property. */
    private boolean linkVisited = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" link label properties ">
    
    /** Gets the color used to display a link.
     * @return the color used to display a link.
     */
    public Color getLinkColor()
    {
	return linkColor;
    }
    
    /** Sets the color used to display a link.
     * @param value the color used to display a link.
     */
    public void setLinkColor(final Color value)
    {
	Color oldValue = linkColor;
	linkColor = value;
	buildLabelText();
	firePropertyChange(LINKCOLOR, oldValue, linkColor);
    }
    
    // TODO: determine if there is a system color for the default link color
    /** internal data for property. */
    private Color linkColor = Color.BLUE;
    
    
    /** Gets the color used to display a visited link.
     * @return the color used to display a visited link.
     */
    public Color getVisitedLinkColor()
    {
	return visitedLinkColor;
    }
    
    /** Sets the color used to display a visited link.
     * @param value the color used to display a visited link.
     */
    public void setVisitedLinkColor(final Color value)
    {
	Color oldValue = visitedLinkColor;
	visitedLinkColor = value;
	buildLabelText();
	firePropertyChange(VISITEDLINKCOLOR, oldValue, visitedLinkColor);
    }
    
    // TODO: determine if there is a system color for the default visited link color
    /** internal data for property. */
    private Color visitedLinkColor = Color.MAGENTA;
    
    /** Alwasy underline the link text. */
    public static final int ALWAYSUNDERLINE = 0;
    /** Only underline the link text when the mouse cursor hovers over the link label. */
    public static final int HOVERUNDERLINE = 1;
    /** Never underline the link text. */
    public static final int NEVERUNDERLINE = 2;
    
    
    /**	Gets the behavior setting of a link.
     * @return ALWAYSUNDERLINE, HOVERUNDERLINE or NEVERUNDERLINE
     */
    public int getLinkBehavior()
    {
	return linkBehavior;
    }
    
    /**	Sets the behavior setting of a link.
     * @param value must be one of ALWAYSUNDERLINE, HOVERUNDERLINE or NEVERUNDERLINE
     */
    public void setLinkBehavior(final int value)
    {
	if(value < ALWAYSUNDERLINE || value > NEVERUNDERLINE)
	{
	    throw new InvalidParameterException("invalid value for link behavior.");
	}
	int oldValue = linkBehavior;
	linkBehavior = value;
	buildLabelText();
	firePropertyChange(LINKBEHAVIOR, oldValue, linkBehavior);
    }
    
    /** internal data for property. */
    private int linkBehavior = ALWAYSUNDERLINE;
    
    
    /** Sets the text this component will display.
     * @param value the text this component will display
     */
    public void setText(final String value)
    {
	String oldValue = unmodifiedText;
	unmodifiedText = value;
	buildLabelText();
	firePropertyChange("text", oldValue, unmodifiedText);
    }
    /** internal data for property. */
    private String unmodifiedText;
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" methods ">
    
    /** Build the strings for the displayed and hover text based upon the JLinkLabel properties.
     * The label text is an html format string using font color="" and u to implement the underline
     * and the colors.
     */
    private void buildLabelText()
    {
	Color color = linkColor;
	if(isVisited())
	{
	    color = visitedLinkColor;
	}
	normalText = buildText(color, linkBehavior == ALWAYSUNDERLINE);
	underlineText = buildText(color, linkBehavior != NEVERUNDERLINE);
	super.setText(normalText);
    }
    /** internal data for property. */
    private String normalText;
    /** internal data for property. */
    private String underlineText;
    
    /** Build a label string from unmodified text, a link color, and an underline status.
     * @param fontColor the color used to draw the link
     * @param underline flag signifying whether to draw the underline
     * @return label text
     */
    private String buildText(final Color fontColor, final boolean underline)
    {
	String returnValue = unmodifiedText;
	if(returnValue != null && returnValue.length() !=0)
	{
	    String linkOpen = "";
	    String linkClose = "";
	    if(fontColor != getForeground())
	    {
		String hexRed = Integer.toHexString(fontColor.getRed());
		if(hexRed.length() == 1) hexRed = "0" + hexRed;
		String hexGreen = Integer.toHexString(fontColor.getGreen());
		if(hexGreen.length() == 1) hexGreen = "0" + hexGreen;
		String hexBlue = Integer.toHexString(fontColor.getBlue());
		if(hexBlue.length() == 1) hexBlue = "0" + hexBlue;
		String hexColor = hexRed + hexGreen + hexBlue;
		linkOpen = "<font color=" + hexColor + ">";
		linkClose = "</font>";
	    }
	    if(underline)
	    {
		linkOpen = "<u>" + linkOpen;
		linkClose += "</u>";
	    }
	    
	    
	    if(linkOpen.length() > 0  && startPosition < returnValue.length())
	    {
		int actualStart = startPosition;
		if(actualStart < 0)
		{
		    actualStart = 0;
		}
		
		if(startPosition > returnValue.length())
		{
		    actualStart = returnValue.length();
		}
		
		int actualLength = linkLength;
		if(actualLength == 0)
		{
		    actualLength = returnValue.length();
		}
		
		int end = returnValue.length();
		if(startPosition + actualLength < end)
		{
		    end = startPosition + actualLength;
		}
		
		String temp = returnValue.substring(0, actualStart);
		temp += linkOpen;
		if(actualStart < returnValue.length())
		{
		    temp += returnValue.substring(actualStart, end);
		}
		temp += linkClose;
		if(end < returnValue.length())
		{
		    temp += returnValue.substring(end);
		}
		returnValue = temp;
	    }
	    returnValue = "<html>" + returnValue + "</html>";
	}
	return returnValue;
    }
    
    /** Reset all properites to defaults. */
    private void clearProperties()
    {
	setText(null);
	setStart(0);
	setLength(0);
	setLinkColor(Color.BLUE);
	setVisitedLinkColor(Color.MAGENTA);
	setVisited(false);
	setLinkBehavior(ALWAYSUNDERLINE);
	setActionCommand(null);
    }
    
    // </editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" MouseListener implementation ">
    
    /** Invoked when a mouse button has been pressed on a component.
     * @param e MouseEvent
     */
    public void mouseReleased(final java.awt.event.MouseEvent e)
    {
    }
    
    /** Invoked when a mouse button has been released on a component.
     * @param e MouseEvent
     */
    public void mousePressed(final java.awt.event.MouseEvent e)
    {
    }
    
    /** Invoked when the mouse exits a component.
     * @param e MouseEvent
     */
    public void mouseExited(final java.awt.event.MouseEvent e)
    {
	// return the text to normal
	super.setText(normalText);
	// change back to the default
	setCursor(Cursor.getDefaultCursor());
    }
    
    /** Invoked when the mouse enters a component.
     * @param e MouseEvent
     */
    public void mouseEntered(final java.awt.event.MouseEvent e)
    {
	// change the text to an underline
	super.setText(underlineText);
	// change the cursor to a hand
	setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    /** Invoked when the mouse button has been clicked (pressed and released) on a component.
     * @param e MouseEvent
     */
    public void mouseClicked(final java.awt.event.MouseEvent e)
    {
	fireActionPerformed();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" action and listener ">
    
    /** Gets the action command for this JLinkLabel.
     * @return the action command
     */
    public String getActionCommand()
    {
	return command;
    }
    
    /** Sets the action command for this JLinkLabel.
     * @param value the action command
     */
    public void	setActionCommand(final String value)
    {
	command = value;
    }
    
    /** internal data for property. */
    private String command;
    
    
    /** Adds an ActionListener to the JLinkLabel.
     * @param listener the ActionListener to be added
     */
    public void addActionListener(final ActionListener listener)
    {
	listenerList.add(ActionListener.class, listener);
    }
    
    /** Removes an ActionListener from the JLinkLabel.
     * @param listener the ActionListener to be removed
     */
    public void removeActionListener(final ActionListener listener)
    {
	listenerList.remove(ActionListener.class, listener);
    }
    
    /** Notify all action listeners that have registered interest for
     * notification on action events.  The event instance
     * is created using the parameters passed into the fire method.
     */
    protected void fireActionPerformed()
    {
		ActionEvent event = new ActionEvent(this, 0, getActionCommand());
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2)
		{
		    if (listeners[i]==ActionListener.class)
		    {
			((ActionListener)listeners[i+1]).actionPerformed(event);
		    }
		}
    }
    
    /** Returns the current action.
     * @return the current action
     */
    public Action getAction()
    {
	return action;
    }
    
    /** Sets the action.
     * @param value The new Action
     */
    public void setAction(final Action value)
    {
		Action oldValue = action;
		action = value;
		if(oldValue != null)
		{
		    removeActionListener(oldValue);
		    oldValue.removePropertyChangeListener(actionPropertyChangeListener);
		}
		if(action == null)
		{
		    clearProperties();
		}
		else
		{
		    configurePropertiesFromAction(action);
		    addActionListener(action);
		    actionPropertyChangeListener = new LinkLabelPropertyChangeListener(this, action);
		    action.addPropertyChangeListener(actionPropertyChangeListener);
		}
		
		firePropertyChange("action", oldValue, action);
    }
    
    /** internal data for property. */
    private Action action;
    /** internal data for property. */
    private PropertyChangeListener actionPropertyChangeListener;
    
    /** A nested class that serves as a property change listener that routes events
     * back to the JLinkLabel.
     */
    private class LinkLabelPropertyChangeListener implements PropertyChangeListener
    {
	/** internal data for property.*/
	private JLinkLabel linkLabel;
	/** internal data for property.*/
	private Action action;
	
	/** Constructs the property chang listener.
	 * @param l the JLinkLabel to update
	 * @param a the action used in the update
	 */
	public LinkLabelPropertyChangeListener(final JLinkLabel l, final Action a)
	{
	    linkLabel = l;
	    action = a;
	}
	
	/** This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
	 */
	public void propertyChange(final PropertyChangeEvent evt)
	{
	    linkLabel.actionPropertyChanged(action, evt.getPropertyName());
	}
    }
    
    /** Sets the JLinkLabel properties from the properties in the action.
     * @param value the source of the property values
     */
    private void configurePropertiesFromAction(final Action value)
    {
	setText((String)value.getValue(Action.NAME));
	setEnabled(value.isEnabled());
	setToolTipText((String)value.getValue(Action.SHORT_DESCRIPTION));
	setIcon((Icon)value.getValue(Action.SMALL_ICON));
	setActionCommand((String)value.getValue(Action.ACTION_COMMAND_KEY));
	setLinkBehaviorFromAction(value);
	setStartFromAction(value);
	setLengthFromAction(value);
	setVisitedFromAction(value);
	setLinkColorFromAction(value);
	setVisitedLinkColorFromAction(value);
    }
    
    /** Updates the link label's properties in response to property changes in the
     * associated action.
     * @param value the source Action
     * @param propertyName the property that changed on the action
     */
    protected void actionPropertyChanged(final Action value, final String propertyName)
    {
	if (propertyName == Action.NAME)
	{
	    setText((String)value.getValue(Action.NAME));
	}
	else if (propertyName == "enabled")
	{
	    setEnabled(value.isEnabled());
	}
	else if (propertyName == Action.SHORT_DESCRIPTION)
	{
	    setToolTipText((String)value.getValue(Action.SHORT_DESCRIPTION));
	}
	else if (propertyName == Action.SMALL_ICON)
	{
	    setIcon((Icon)value.getValue(Action.SMALL_ICON));
	}
	else if (propertyName == Action.ACTION_COMMAND_KEY)
	{
	    setActionCommand((String)value.getValue(Action.ACTION_COMMAND_KEY));
	}
	else if (propertyName == LINKBEHAVIOR)
	{
	    setLinkBehaviorFromAction(value);
	}
	else if (propertyName == START)
	{
	    setStartFromAction(value);
	}
	else if (propertyName == LENGTH)
	{
	    setLengthFromAction(value);
	}
	else if (propertyName == VISITED)
	{
	    setVisitedFromAction(value);
	}
	else if (propertyName == LINKCOLOR)
	{
	    setLinkColorFromAction(value);
	}
	else if (propertyName == VISITEDLINKCOLOR)
	{
	    setVisitedLinkColorFromAction(value);
	}
    }
    
    /** Sets the link behavior.
     * @param value the source Action
     */
    private void setLinkBehaviorFromAction(final Action value)
    {
	int behavior = ALWAYSUNDERLINE;
	Integer property = (Integer)value.getValue(LINKBEHAVIOR);
	if(property != null)
	{
	    behavior = ((Integer)property).intValue();
	}
	setLinkBehavior(behavior);
    }
    
    /** Sets the start value.
     * @param value the source Action
     */
    private void setStartFromAction(final Action value)
    {
	int start = 0;
	Integer property = (Integer)value.getValue(START);
	if(property != null)
	{
	    start = ((Integer)property).intValue();
	}
	setStart(start);
    }
    
    /** Sets the length value.
     * @param value the source Action
     */
    private void setLengthFromAction(final Action value)
    {
	int length = 0;
	Integer property = (Integer)value.getValue(LENGTH);
	if(property != null)
	{
	    length = ((Integer)property).intValue();
	}
	setLength(length);
    }
    
    /** Sets the link color.
     * @param value the source Action
     */
    private void setLinkColorFromAction(final Action value)
    {
	Color color = Color.BLUE;
	Color property = (Color)value.getValue(LINKCOLOR);
	if(property != null)
	{
	    color = property;
	}
	setLinkColor(color);
    }
    
    /** Sets the visited link color.
     * @param value the source Action
     */
    private void setVisitedLinkColorFromAction(final Action value)
    {
	Color color = Color.MAGENTA;
	Color property = (Color)value.getValue(VISITEDLINKCOLOR);
	if(property != null)
	{
	    color = property;
	}
	setVisitedLinkColor(color);
    }
    
    /** Sets the visited value.
     * @param value the source Action
     */
    private void setVisitedFromAction(final Action value)
    {
	boolean visited = false;
	Boolean property = (Boolean)value.getValue(VISITED);
	if(property != null)
	{
	    visited = ((Boolean)property).booleanValue();
	}
	setVisited(visited);
    }
    
    // </editor-fold>
   
}
