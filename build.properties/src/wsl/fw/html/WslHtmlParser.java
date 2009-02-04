/** $Id: WslHtmlParser.java,v 1.1 2004/09/17 02:25:49 tecris Exp $
 */
package wsl.fw.html;

import java.io.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

/**
 *
 * WslHtmlParser - html parser .
 * To be used for messages that are presented as HTML.
 * 
 */
public class WslHtmlParser extends HTMLEditorKit.ParserCallback {

	private StringBuffer buf;
	private boolean 
		hasLink = false,
		toIgnore = false;
	
	public void 
	handleText (
	 char[] data, 
	 int pos)
	{
		// read the actual content of the html page
		if(!toIgnore)
			buf.append(data);
	}

	public void 
	handleStartTag (
	 HTML.Tag tag, 
	 MutableAttributeSet a, 
	 int pos)
	{
		//	keep the links
		if (tag ==HTML.Tag.A)
		{
			String href=(String)a.getAttribute(HTML.Attribute.HREF);
			if (href != null)
			{
				buf.append ("<" + HTML.Tag.A + " " +HTML.Attribute.HREF+ "=\""+href + "\">");
				hasLink = true;
			}
		} else if (tag == HTML.Tag.P || tag == HTML.Tag.TR)
		{
			// insert break lines
			buf.append("\n");
		} else if (tag == HTML.Tag.STYLE)
			toIgnore = true;
	}

	public void 
	handleEndTag (
	 HTML.Tag tag, 
	 int pos)
	{
		if (tag == HTML.Tag.A && hasLink)
		{
			buf.append("</"+ HTML.Tag.A + ">");
			hasLink = false;
		} else if (tag == HTML.Tag.STYLE)
		toIgnore = false;
	}
	
	public void 
	handleSimpleTag 
	(
	 HTML.Tag tag, 
	 MutableAttributeSet a, 
	 int pos)
	{
		// insert break lines
		if (tag == HTML.Tag.BR)
			buf.append("\n");	
	}

	  
	public String 
	parseHtml (
	 String htmlString)
	{
		buf = new StringBuffer ();
	  	try
		{	  		
	  		Reader r = new StringReader (htmlString);
	  		ParserDelegator parser = new ParserDelegator();
	  		parser.parse(r, this, true);	 
	  		return buf.toString ();
		} catch (IOException ioex)
		{
			ioex.printStackTrace();
			return htmlString;
		}
	}
}
