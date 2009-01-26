package com.framedobjects.dashwell.servlets;

import http.utils.multipartrequest.MultipartRequest;
import http.utils.multipartrequest.ServletMultipartRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.framedobjects.dashwell.handlers.FileUploadHandler;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.XmlFormatter;

public class OL_FileUploadServlet extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(OL_FileUploadServlet.class.getName());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("Received file upload GET request");
		String action = request.getParameter("action");
		System.out.println("GET ----------------- Received file upload POST request" + action);
		Element root = null;
		if (action != null){
			if (action.equalsIgnoreCase("uploadLanguageFile")){
				String language = request.getParameter("language");
				String languageId = request.getParameter("languageId");
				int intLanguageId = Integer.parseInt(languageId);
				if (language != null){
					// Check for duplicate language name.
					if (new FileUploadHandler().isDuplicateLanguageName(intLanguageId, language)){
						// Return duplicate language name error.
						root = new XmlFormatter().setResult("Duplicate language name.");
					} else {
						// Store the language name in the session and return OK.
						logger.debug("Storing language: " + language + " in session.");
						HttpSession session = request.getSession();
						logger.warn("GET SessionID: " + session.getId());
						session.setAttribute(Constants.SESSION_NEW_LANGUAGE, language);
						root = new XmlFormatter().setResult("OK");
						language = (String)session.getAttribute(Constants.SESSION_NEW_LANGUAGE);
						System.out.println("GET ----------------- Received file upload POST request [" + language + "]");
												
					}
				} else {
					root = new XmlFormatter().setResult("Language file missing.");
				}
			} else {
				root = new XmlFormatter().undefinedAction();
			}
		} else {
			root = new XmlFormatter().undefinedAction();
		}
		
		// Return the XML-formatted reply.
	    response.setHeader ("Pragma",        "no-cache");
	    response.setHeader ("Cache-Control", "no-cache");
	    response.setContentType("text/xml; charset=UTF-8");
	    String xml = new XMLOutputter().outputString(new Document(root));        
	    logger.debug("xml: " + xml);
	    
	    ServletOutputStream out = response.getOutputStream();
	    out.println(xml);	    
	}

	/**
	 * Deals with the actual file upload.
	 * 
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			if (request.getContentLength() > 0) {
			
				ServletMultipartRequest upload = new ServletMultipartRequest(request, 104857600, null); // 100 Mb maximum
				String fileName = upload.getBaseFilename("Filedata");
				logger.info("Got file: " + fileName);
				upload.getContentType("Filedata");
				logger.info("File size: " + MultipartRequest.MAX_READ_BYTES);
				HttpSession session = request.getSession();
				logger.warn("POST SessionID: " + session.getId());
				String language = (String)session.getAttribute(Constants.SESSION_NEW_LANGUAGE);
				logger.info("Need to store language: " + language + " with file: " + fileName);
				
				System.out.println("POST ----------------- Received file upload POST request [" + fileName + "]---["+ language +"]");
				
				// Test the previous file stored in the session.
				logger.debug("File stored processed previously: " + (String)session.getAttribute("fileName"));
				// Put this file in the session.
				session.setAttribute("fileName", fileName);
				
				InputStream is = upload.getFileContents("Filedata");
				String filePath = Constants.LANGUAGE_FILE_PATH + fileName;
				logger.debug("filePath: " + filePath);
				FileOutputStream fos = new FileOutputStream(filePath);
				
				byte[] buffer = new byte[1024];
				int len = 0;
				
				while (len != (-1)) {
					len = is.read(buffer, 0, 1024);
					if (len != (-1)) fos.write(buffer, 0, len);
				}
				
				fos.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
}
