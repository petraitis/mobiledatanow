package wsl.fw.html;

// imports
import javax.servlet.http.HttpServletResponse;
import wsl.fw.util.Util;
import wsl.fw.servlet.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class WslHtmlUtil
{
    //--------------------------------------------------------------------------
    // utilities

    /**
     * Make a string containing the href tp the servlet, for use in get or post.
     * @param response, the HttpServletResponse for this servlet call, used for
     *    encodeURL so that sessons will still work even if cookies are not
     *    supported by the browser.
     */
    public static String makeHref(HttpServletResponse response, String url)
    {
        return makeHref(response, url, null);
    }

    /**
     * Make a string for use in GET hrefs specifying an action.
     * @param response, the HttpServletResponse for this servlet call, used for
     *    encodeURL so that sessons will still work even if cookies are not
     *    supported by the browser.
     * @param action, the action value to set for ServletBase.RP_ACTION,
     *   if null no action is specified.
     */
    public static String makeHref(HttpServletResponse response, String url, String action)
    {
        return makeHref(response, url, action, null);
    }

    /**
     * Make a string for use in GET hrefs specifying an action.
     * @param response, the HttpServletResponse for this servlet call, used for
     *    encodeURL so that sessons will still work even if cookies are not
     *    supported by the browser.
     * @param action, the action value to set for ServletBase.RP_ACTION,
     *   if null no action is specified.
     * @param param the parameter name
     * @param value the value for the parameter
     */
    public static String makeParamHref(HttpServletResponse response, String url,
        String action, String param, String value)
    {
        // make the base href
        String href = makeHref(response, action, null);

        // add the parameter
        href += "&" + param + "=" + value;
        return href;
    }

    /**
     * Add a parameter to an HREF
     * @param href the href to add the param to
     * @param param the param name
     * @param value the value of the param
     */
    public static String addHrefParam(String href, String param, Object value)
    {
        // add the parameter
        href += "&" + param + "=" + value.toString();
        return href;
    }

    /**
     * Make a string for use in GET hrefs specifying an action.
     * @param response, the HttpServletResponse for this servlet call, used for
     *    encodeURL so that sessons will still work even if cookies are not
     *    supported by the browser.
     * @param action, the action value to set for ServletBase.RP_ACTION,
     *   if null no action is specified.
     * @param subAction, the action value to set for ServletBase.RP_SUB_ACTION,
     *   if null no sub action is specified.
     * @param param the parameter name
     * @param value the value for the parameter
     */
    public static String makeParamHref(HttpServletResponse response, String url,
        String action, String subAction, String param, String value)
    {
        // make the base href
        String href = makeHref(response, url, action, subAction);

        // add the parameter
        href += "&" + param + "=" + value;
        return href;
    }

    /**
     * Make a string for use in GET hrefs specifying an action.
     * @param response, the HttpServletResponse for this servlet call, used for
     *    encodeURL so that sessons will still work even if cookies are not
     *    supported by the browser.
     * @param action, the action value to set for ServletBase.RP_ACTION,
     *   if null no action is specified.
     * @param subAction, the subaction value to set for
     *   ServletBase.RP_SUB_ACTION, if null no subaction is specified.
     *   Ignored if action is null.
     */
    public static String makeHref(HttpServletResponse response, String url,
        String action, String subAction)
    {
        // basic href is the url
        StringBuffer href = new StringBuffer();
        href.append(url);
        // if there is an action specified add it in HTTP GET format
        if (action != null)
        {
            href.append('?');
            href.append(ServletBase.RP_ACTION);
            href.append('=');
            href.append(action);
            // if there is a subaction add it
            if (subAction != null)
            {
                href.append('&');
                href.append(ServletBase.RP_SUB_ACTION);
                href.append('=');
                href.append(subAction);
            }
        }

        // finally encode the URL with the session info so we can support
        // sessions even if cookies are not available. also escape wml chars
        return response.encodeURL(href.toString());
    }

    /**
     * Escape a string for a GET or POST URL
     */
    public static String esc(String str)
    {
        if (Util.isEmpty(str))
            return "";
        return str;
    }
}