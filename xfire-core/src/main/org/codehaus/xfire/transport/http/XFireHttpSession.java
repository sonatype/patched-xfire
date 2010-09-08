package org.codehaus.xfire.transport.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.xfire.transport.Session;

/**
 * The default servlet session implementation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireHttpSession
    implements Session
{
    public final static String HTTP_SERVLET_REQUEST_KEY = "xfire.httpServletRequest";
    
    private HttpServletRequest request;
    
    private HttpSession session;
    
	public XFireHttpSession( HttpServletRequest request )
    {
        this.request = request;
    }
    
	/**
	 * @see org.codehaus.xfire.transport.Session#get(java.lang.Object)
	 */
	public Object get(Object key)
	{
		return getSession().getAttribute((String)key);
	}

	/**
	 * @see org.codehaus.xfire.transport.Session#put(java.lang.Object, java.lang.Object)
	 */
	public void put(Object key, Object value)
	{
		getSession().setAttribute((String)key, value);
	}

    public HttpSession getSession()
    {
    	if ( session == null )
        {
    		session = request.getSession();
        }
        
        return session;
    }
}
