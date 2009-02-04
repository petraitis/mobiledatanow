//==============================================================================
// ServletDelegate.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.servlet;

import wsl.fw.util.Util;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

//------------------------------------------------------------------------------
/**
 * A delegate class that can be subclassed and used by the main controller
 * servlet if it is desirable to seperate some of the servlet functions into a
 * separate class. Should not be shared or reused by the servlet as this could
 * result in concurrency problems.
 */
public abstract class ServletDelegate
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/servlet/ServletDelegate.java $ ";

    // attributes
    public ServletBase         _servlet;
    public HttpServletRequest  _request;
    public HttpServletResponse _response;

    //--------------------------------------------------------------------------
    /**
     * Default constructor, used when passing to ServletBase.delegate().
     * If you are no using delegate then you MUST manually set the _servlet,
     * _request and _response members before calling run.
     */
    public ServletDelegate()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Abstract function to be implemented by the subclass to perform the
     * delegated functions.
     * @throws IOException, standard exception thrown by servlet methods.
     * @throws ServletException, standard exception thrown by servlet methods.
     */
    abstract public void run() throws IOException, ServletException;

    //--------------------------------------------------------------------------
    /**
     * Forward to another url, the caller should not write any responses.
     * @param url, the url to forward to.
     * @throws IOException if the forwarded resource throws the exception.
     * @throws ServletException if the forwarded resource throws the exception.
     */
    public void forward(String url)
        throws IOException, ServletException
    {
        _servlet.forward(url, _request, _response);
    }

    //--------------------------------------------------------------------------
    /**
     * Include another url.
     * @param url, the url with the data to include.
     * @throws IOException if the forwarded resource throws the exception.
     * @throws ServletException if the forwarded resource throws the exception.
     */
    public void include(String url)
        throws IOException, ServletException
    {
        _servlet.include(url, _request, _response);
    }

    //--------------------------------------------------------------------------
    /**
     * Set a session variable.
     * @param request, the caller's HttpServletRequest.
     * @param name, the name of the session variable to set.
     * @param value, the value to set, or remove if null.
     */
    public void setSessionVar(String name, Object value)
    {
        _servlet.setSessionVar(_request, name, value);
    }

    //--------------------------------------------------------------------------
    /**
     * Get a session variable.
     * @param name, the name of the session variable to get.
     * @return the value of the variable or null if it is not set.
     */
    public Object getSessionVar(String name)
    {
        return _servlet.getSessionVar(_request, name);
    }


    //--------------------------------------------------------------------------
    /**
     * Remove a session variable.
     * @param name, the name of the session variable to get.
     * @return the value of the variable or null if it is not set.
     */
    public Object removeSessionVar(String name)
    {
        return _servlet.removeSessionVar(_request, name);
    }

    //--------------------------------------------------------------------------
    /**
     * Delegate the handling of this servlet action to a ServletDelegate.
     * General use would be delegate(new DelegateSubclass());
     */
    public void delegate(ServletDelegate delegate)
        throws ServletException, IOException
    {
        _servlet.delegate(delegate, _request, _response);
    }
}

//==============================================================================
// end of file ServletDelegate.java
//==============================================================================
