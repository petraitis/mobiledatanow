package com.framedobjects.dashwell.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import wsl.fw.exception.MdnException;
import wsl.mdn.dataview.LanguageDobj;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.XmlFormatter;

public class OL_GuiServlet extends HttpServlet {

  private static Logger logger = Logger
      .getLogger(OL_GuiServlet.class.getName());

  /**
   * The doGet method of the servlet. <br>
   * 
   * This method is called when a form has its tag value method equals to get.
   * 
   * @param request
   *          the request send by the client to the server
   * @param response
   *          the response send by the server to the client
   * @throws ServletException
   *           if an error occurred
   * @throws IOException
   *           if an error occurred
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    logger.debug("URI: " + request.getRequestURI());
    logger.debug("Query string: " + request.getQueryString());
    
    Enumeration parameters = request.getParameterNames();
    while (parameters.hasMoreElements()){
      String p = (String)parameters.nextElement();
      logger.debug("-- Parameter: " + p + ": " + request.getParameter(p));
    }
    
    String action = request.getParameter(Constants.REQUEST_PARAM_ACTION);
    logger.debug("current action is: " + action);
    Element root = null;
    if (action != null){
      if (action.equalsIgnoreCase(Constants.ACTION_NAVIGATION)){
        root = navigationAction(request);
      } else if (action.equalsIgnoreCase(Constants.ACTION_GUI_DEFINITION)){
        root = getGuiDefForLocale(request);
      } else {
          root = new XmlFormatter().undefinedAction();
      }
    } else {
        root = new XmlFormatter().undefinedAction();
    }
    
    root.detach();
    
    // Return the XML-formatted reply.
    response.setHeader ("Pragma",        "no-cache");
    response.setHeader ("Cache-Control", "no-cache");  
    response.setContentType("text/xml; charset=UTF-8");
    String xml = new XMLOutputter().outputString(new Document(root));        
//    logger.info("xml: " + xml);        
    
    ServletOutputStream out = response.getOutputStream();
    out.println(xml);
  }
  
  /**
   * Retrieves the XML language file. Defaults to English if an unknown language
   * has been requested. Puts the selected language in the session.
   * @param request
   * @return The XML language file.
   */
  private Element getGuiDefForLocale(HttpServletRequest request){
    String file = null;
    String locale = request.getParameter("language");
//    if (locale != null){
//      file = "gui_" + locale + ".xml";
//    } else {
//      file = "gui_en.xml";
//    }
    file = "gui_en.xml"; // Provide a default language file.
//    /*if (locale.equalsIgnoreCase("chinese (simple)")){
//      file = "gui_cn_simple.xml";
//    } else if (locale.equalsIgnoreCase("chinese (traditional)")){
//      file = "gui_cn_trad.xml";
//    } else if (locale.equalsIgnoreCase("german")){
//      file = "gui_de.xml";
//    } else*/ 
//    if (locale.equalsIgnoreCase("english")) {
//      file = "gui_en.xml";
//    } /*else if (locale.equalsIgnoreCase("spanish")) {
//      file = "gui_sp.xml";
//    } else if (locale.equalsIgnoreCase("japanese")) {
//      file = "gui_jp.xml";
//    } else if (locale.equalsIgnoreCase("russian")) {
//      file = "gui_ru.xml";
//    }*/else if (locale.equalsIgnoreCase("Australian English")) {
//      file = "gui_au.xml";
//    }
    
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	List<LanguageDobj> languages;
	try {
		languages = dataAgent.getAllLanguages();
		for (LanguageDobj lang : languages){
			if (locale.equalsIgnoreCase(lang.getName())){
				file = lang.getFileName();
			}
		}

	} catch (MdnException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}    
    
    // Set the selected language on the session.
    request.getSession().setAttribute(Constants.SESSION_LANGUAGE_FILE, file);
    Element root = getXMLLanguageFile(file);
    if (root == null){
      // Provide the default English.
      root = getXMLLanguageFile("gui_en.xml");
    }
    return root;
  }
  
  private Element getCurrentLanguageFile(HttpServletRequest request){
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    if (file == null){
      file = "gui_en.xml";
    }
    return getXMLLanguageFile(file);
  }

  /**
   * Retrieves the XML Language file.
   * @param file
   * @return
   */
  private Element getXMLLanguageFile(String file) {
    Element root = null;
    SAXBuilder builder = new SAXBuilder();
    try {
      Document doc = builder.build(Constants.LANGUAGE_FILE_URL + file);
      root = doc.getRootElement();
    } catch (JDOMException e) {
        logger.error("Exception parsing GUI XML document");
        e.printStackTrace();
    } catch (IOException ioe){
      logger.error("IOException parsing GUI XML document");
      ioe.printStackTrace();
    }
    return root;
  }

  private Element navigationAction(HttpServletRequest request){
    return new XmlFormatter().navigation();
  }
}
