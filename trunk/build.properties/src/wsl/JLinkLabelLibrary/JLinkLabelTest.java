package wsl.JLinkLabelLibrary;
/* JLinkLabelTest.java
 * JUnit based test
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import junit.framework.*;

/** TestCase for JLinkLabel
 * @author tim
 */
public class JLinkLabelTest extends TestCase
{

    /** what happens when a null text value is passed in. */
    public void testNullText()
    {
	JLinkLabel label = new JLinkLabel();
	label.setText(null);
	assertNull("expected a null text value", label.getText());
    }
    
    /** test setting link behavior. */
    public void testBehavior()
    {
	JLinkLabel label = new JLinkLabel();
	
	assertEquals("unexpected default behavior", JLinkLabel.ALWAYSUNDERLINE, label.getLinkBehavior());
	
	String text = "Once upon a time";
	label.setText(text);
	assertEquals("unexpected label text", "<html><u><font color=0000ff>Once upon a time</font></u></html>", label.getText());
	
	// hover underline
	label.setLinkBehavior(JLinkLabel.HOVERUNDERLINE);
	assertEquals("unexpected behavior", JLinkLabel.HOVERUNDERLINE, label.getLinkBehavior());
	assertEquals("unexpected label text", "<html><font color=0000ff>Once upon a time</font></html>", label.getText());
    }
    
    /** test link start and length properties. */
    public void testLinkArea()
    {
	JLinkLabel label = new JLinkLabel();
	
	// test default
	assertEquals("unexpected start value", 0, label.getStart());
	assertEquals("unexpected length value", 0, label.getLength());
	
	// test full text when values are default
	String text = "Once upon a time";
	label.setText(text);
	
	assertEquals("unexpected start value", 0, label.getStart());
	assertEquals("unexpected length value", text.length(), label.getLength());
	
	// test link in middle of string
	label.setStart(5);
	label.setLength(4);
	assertEquals("unexpected label text", "<html>Once <u><font color=0000ff>upon</font></u> a time</html>", label.getText());
	
	// test link at begining of string
	label.setStart(0);
	label.setLength(4);
	assertEquals("unexpected label text", "<html><u><font color=0000ff>Once</font></u> upon a time</html>", label.getText());
	
	// test link at end of string
	label.setStart(12);
	label.setLength(4);
	assertEquals("unexpected label text", "<html>Once upon a <u><font color=0000ff>time</font></u></html>", label.getText());
	
	// test link overlapping end of string
	label.setStart(14);
	label.setLength(4);
	assertEquals("unexpected label text", "<html>Once upon a ti<u><font color=0000ff>me</font></u></html>", label.getText());
	
	// test link beyond end of string
	label.setStart(18);
	label.setLength(4);
	assertEquals("unexpected label text", "<html>Once upon a time</html>", label.getText());
    }
    
    /** test setting link visited. */
    public void testVisited()
    {
	JLinkLabel label = new JLinkLabel();
	
	assertEquals("unexpected default behavior", false, label.isVisited());
	
	String text = "Once upon a time";
	label.setText(text);
	System.out.println(label.getText());
	assertEquals("unexpected default label text", "<html><u><font color=0000ff>Once upon a time</font></u></html>", label.getText());
	
	// hover underline
	label.setVisited(true);
	System.out.println(label.getText());
	assertEquals("unexpected behavior", true, label.isVisited());
	assertEquals("unexpected visted label text", "<html><u><font color=ff00ff>Once upon a time</font></u></html>", label.getText());
    }
    
    /** test the the action listener support by building a label, setting the action command,
     * registering a listener, and triggering the action.
     */
    public void testActionListener()
    {
	String linkCommand = "http://www.exotribe.com";
	JLinkLabel link = new JLinkLabel();
	link.setActionCommand(linkCommand);
	assertEquals("unexptected action command property", linkCommand, link.getActionCommand());
	
	JLinkLabelActionListener listener = new JLinkLabelActionListener();
	link.addActionListener( listener);
	
	link.fireActionPerformed();
	
	// ensure that actionPerformed was called with the correct action command.
	assertTrue("actionPerformed was not called", listener.actionCalled);
	assertEquals("unexptected action command listener", linkCommand, listener.actionCommand);
	
	// reset test variables and remove the listener
	listener.actionCalled = false;
	listener.actionCommand = "";
	link.removeActionListener( listener);
	
	link.fireActionPerformed();
	
	// ensure that actionPerformed was called with the correct action command.
	assertFalse("actionPerformed was called (it should not have been)", listener.actionCalled);
	assertEquals("unexptected action command in listener", "", listener.actionCommand);
    }
    

    
    /** test the the action support by building a label, setting the action, and triggering the action.
     */
    public void testAction()
    {
	String linkText = "Once upon a time";
	String linkCommand = "http://www.exotribe.com";
	
	JLinkLabel label = new JLinkLabel();
	
	JLinkAction action = new JLinkAction();
	action.putValue(Action.NAME, linkText);
	action.putValue(Action.ACTION_COMMAND_KEY, linkCommand);
	
	label.setAction(action);
	System.out.println("testAction: " + label.getText());
	assertEquals("unexpected label text", "<html><u><font color=0000ff>Once upon a time</font></u></html>", label.getText());
	assertEquals("unexptected action command property", linkCommand, label.getActionCommand());
	
	// test changing properties from action
	boolean exceptionThrown = false;
	try
	{
	    action.putValue(JLinkLabel.LINKBEHAVIOR, "dummy text value");
	}
	catch(ClassCastException ex)
	{
	    exceptionThrown = true;    
	}
	assertTrue("expected an exception due to improperty link behavior type", exceptionThrown);
	
	action.putValue(JLinkLabel.LINKBEHAVIOR, new Integer(JLinkLabel.HOVERUNDERLINE));
	assertEquals("unexpected behavior value", JLinkLabel.HOVERUNDERLINE, label.getLinkBehavior());
	
	action.putValue(JLinkLabel.LINKCOLOR, Color.RED);
	assertEquals("unexpected link color value", Color.RED, label.getLinkColor());
		
	action.putValue(JLinkLabel.START, new Integer(5));
	assertEquals("unexpected start value", 5, label.getStart());
	
	action.putValue(JLinkLabel.LENGTH, new Integer(4));
	assertEquals("unexpected length value", 4, label.getLength());
	
	action.putValue(JLinkLabel.VISITED, new Boolean(true));
	assertEquals("unexpected visited value", true, label.isVisited());
	
	action.putValue(JLinkLabel.VISITEDLINKCOLOR, Color.GREEN);
	assertEquals("unexpected visited color value", Color.GREEN, label.getVisitedLinkColor());
		
	// test the action events
	label.fireActionPerformed();
	
	// ensure that actionPerformed was called with the correct action command.
	assertTrue("actionPerformed was not called", action.actionCalled);
	assertEquals("unexptected action command listener", linkCommand, action.actionCommand);
	
	// reset test variables and remove the listener
	action.actionCalled = false;
	action.actionCommand = "";
	label.setAction(null);
	
	label.fireActionPerformed();
	
	// ensure that actionPerformed was called with the correct action command.
	assertFalse("actionPerformed was called (it should not have been)", action.actionCalled);
	assertEquals("unexptected action command in listener", "", action.actionCommand);
    }

}

