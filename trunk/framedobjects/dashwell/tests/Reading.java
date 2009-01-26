package com.framedobjects.dashwell.tests;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class Reading {
  
  public static void main(String[] args){
    Element root = getGuiDefForLocale();
    String xml = new XMLOutputter().outputString(new Document(root));        
    System.out.println(xml);
  }

  private static Element getGuiDefForLocale(){
    String locale = "en";
    Element root = null;
    // Have a default locale.
    SAXBuilder builder = new SAXBuilder();
    try {
      Document doc = builder.build("http://203.98.22.167:8765/dashwell/gui_en.xml");
      System.err.println(doc.toString());
      root = doc.getRootElement();
    } catch (JDOMException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
	  } catch (IOException ioe){
	      
	      ioe.printStackTrace();
	  }
    return root;
  }
}
