//==============================================================================
// ServletBase.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.servlet;

import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.security.SecurityId;
import wsl.fw.security.SecurityException;
import wsl.fw.remote.RmiServant;
import wsl.fw.remote.LocalServerFactory;
import wsl.fw.remote.SecureRegistry;

import java.io.IOException;
import java.util.Vector;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Base class for wsl servlets that make use of RMIServers. Inits its context
 * to RMICLIENT_CONTEXT and assists in settin up the COnfig context.
 * Provides helper functions for RmiServant lookup and dealing with
 * ServletDelegates, forwarded or included pages and session variables.
 * @see ServletDelegate.
 */
public class ServletBase extends HttpServlet
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/servlet/ServletBase.java $ ";

    // resources
    public static final ResId WARNING_DISPATCHER  = new ResId("ServletBase.warning.Dispatcher");
    public static final ResId ERR_CONTEXT  = new ResId("ServletBase.error.Context");
    public static final ResId WARNING_DISPATCHER2  = new ResId("ServletBase.warning.Dispatcher2");
    public static final ResId ERR_CONTEXT2  = new ResId("ServletBase.error.Context2");
    public static final ResId ERR_SESSION  = new ResId("ServletBase.error.Session");

    // HTTP request parameter and session variable constants
    public final static String RP_ACTION     = "servlet.action";
    public final static String RP_SUB_ACTION = "servlet.subAction";
    public final static String SV_ERROR      = "servlet.error";

    // request attribute name for passing temporary subsection info on to a jsp
    public final static String RA_PAGE_ATTRIBUTE = "servlet.pageAttribute";

    // attributes
    protected String         _commandLine[];
    protected SecurityId     _systemId;
    private   SecureRegistry _secureRegistry = null;

    //--------------------------------------------------------------------------
    /**
     * Default constructor
     */
    public ServletBase()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Init method sets up the context and command line params for wsl.
     * @param cfg, the servlet config.
     */
    public void init(ServletConfig cfg) throws ServletException
    {
        super.init(cfg);

        // read the servlet config to build the psuedo command line parameters
        int commandlineIndex = 1;
        String paramName;
        String param;
        Vector params = new Vector();
        while (true)
        {
            // build the name of the param using the servlet commandline prefix
            paramName = CKfw.SERVLET_COMMANDLINE_PREFIX
                + String.valueOf(commandlineIndex++);

            // get the parameter
            param = cfg.getInitParameter(paramName);
            // if the parameter exists then add it, else break out of the loop
            if (param != null)
            {
                params.add(param);
                Log.debug(getClass().getName() + " " + paramName + "=" + param);
            }
            else
                break;
        }

        // save the params as a String[]
        _commandLine = (String[]) params.toArray(new String[0]);

        // load the default or command line Config context, if not specified
        // uses the rmi client context
        Config.getSingleton().addContext(_commandLine, CKfw.RMICLIENT_CONTEXT);

        // may need to call Log.init() here if the Config data is not available
        // in time for the Log to init properly.

        // let LocalServerFactory check for its command line args
        LocalServerFactory.setArgs(_commandLine);

        _systemId = SecurityId.getSystemId();
     }

    //--------------------------------------------------------------------------
    /**
     * Get a reference to a servant object by name.
     * @param servantName, the name of the servant to get.
     * @return the servant.
     * @throws RemoteException if there is an RMI error.
     * @throws SecurityException if the servlet does not have permission to
     *   perform rmi lookus.
     * @throws NotBoundException if the named servant or the secure registry
     *   could not be found.
     */
    public RmiServant lookup(String servantName)
        throws RemoteException, NotBoundException, SecurityException
    {
        SecureRegistry sr;

        synchronized (this)
        {
            // if we do not have a cached secure registry, get one
            if (_secureRegistry == null)
                _secureRegistry = LocalServerFactory.getSecureRegistry();
            sr = _secureRegistry;
        }

        RmiServant servant = null;

        try
        {
            servant = sr.lookup(_systemId, servantName);
        }
        catch (RemoteException e)
        {
            // a remote exception indicates that _secureRegistry may no longer
            // be valid, dispose of it so next time we get a fresh one
            synchronized (this)
            {
                _secureRegistry = null;
            }
            throw e;
        }

        return servant;
    }

    //--------------------------------------------------------------------------
    /**
     * Forward to another url, the caller should not write any responses.
     * @param url, the url to forward to.
     * @param request, the request from the caller.
     * @param response, the response from the caller.
     * @throws IOException if the forwarded resource throws the exception.
     * @throws ServletException if the forwarded resource throws the exception.
     */
    public void forward(String url,
        HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        Log.debug("ServletBase.forward [" + url + "]");

        // get the servlet context
        ServletContext sc = getServletContext();
        if(sc != null)
        {
            //get the request dispatcher and forward to the url
            RequestDispatcher rd = sc.getRequestDispatcher(url);

            if(rd != null)
                rd.forward(request, response);
            else
                Log.warning(WARNING_DISPATCHER.getText()
                    + " [" + url +"]");
        }
        else
            Log.error(ERR_CONTEXT.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Include another url.
     * @param url, the url with the data to include.
     * @param request, the request from the caller.
     * @param response, the response from the caller.
     * @throws IOException if the forwarded resource throws the exception.
     * @throws ServletException if the forwarded resource throws the exception.
     */
    public void include(String url,
        HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        Log.debug("ServletBase.forward [" + url + "]");

        // get the servlet context
        ServletContext sc = getServletContext();
        if(sc != null)
        {
            //get the request dispatcher and forward to the url
            RequestDispatcher rd = sc.getRequestDispatcher(url);
            if(rd != null)
                rd.include(request, response);
            else
                Log.warning(WARNING_DISPATCHER2.getText()
                    + " [" + url +"]");
        }
        else
            Log.error(ERR_CONTEXT2.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Set a session variable.
     * @param request, the caller's HttpServletRequest.
     * @param name, the name of the session variable to set.
     * @param value, the value to set, or remove if null.
     */
    public static void setSessionVar(HttpServletRequest request,
        String name, Object value)
    {
        HttpSession session = request.getSession();
        if(session != null)
        {
            //set the error attribute
            session.removeAttribute(name);
            if (value != null)
                session.setAttribute(name, value);
        }
        else
            Log.error(ERR_SESSION.getText());
    }
    //--------------------------------------------------------------------------
    /**
     * Get a session variable.
     * @param request, the caller's HttpServletRequest.
     * @param name, the name of the session variable to get.
     * @return the value of the variable or null if it is not set.
     */
    public static Object getSessionVar(HttpServletRequest request, String name)
    {
        HttpSession session = request.getSession(false);
        return (session != null) ? session.getAttribute(name) : null;
    }

    //--------------------------------------------------------------------------
    /**
     * Remove a session variable.
     * @param request, the caller's HttpServletRequest.
     * @param name, the name of the session variable to get.
     * @return the value of the variable or null if it is not set.
     */
    public static Object removeSessionVar(HttpServletRequest request, String name)
    {
        // get it
        Object ret = getSessionVar(request, name);
        if(ret != null)
        {
            // remove it
            HttpSession session = request.getSession(false);
            session.removeAttribute(name);
        }
        return ret;
    }

    //--------------------------------------------------------------------------
    /**
     * Add HTTP headers to the servlet output to ensure the page will not be
     * cached by the browser.
     * @param response the servlet response that will recieve the cache control
     *   headers.
     */
    public static void disableCacheHeader(HttpServletResponse response)
    {
        final String CACHE_CONTROL = "Cache-Control";
        final String EXPIRES       = "Expires";
        final String PRAGMA        = "Pragma";

        response.addHeader(CACHE_CONTROL, "must-revalidate");
        response.addHeader(CACHE_CONTROL, "no-cache");
        response.addHeader(CACHE_CONTROL, "max-age=0");
        response.addHeader(EXPIRES, "Mon, 01 Jan 1999 12:00:00 GMT");
        response.addHeader(PRAGMA, "no-cache");
    }

    //--------------------------------------------------------------------------
    /**
     * Delegate the handling of this servlet action to a ServletDelegate.
     * General use would be delegate(new DelegateSubclass(), request, response);
     * @param delegate, the delegate to run.
     * @param request, the HttpServletRequest from the caller, which will be set
     *   into the delegate.
     * @param response, the HttpServletResponse from the caller, which will be
     *   set into the delegate.
     * @throws IOException, standard exception thrown by servlet methods.
     * @throws ServletException, standard exception thrown by servlet methods.
     */
    public void delegate(ServletDelegate delegate,
        HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        // set the calling servlet, request and response into the delegate.
        delegate._servlet = this;
        delegate._request = request;
        delegate._response = response;
        // run the delegate
        delegate.run();
    }
}

//==============================================================================
// end of file ServletBase.java
//==============================================================================
