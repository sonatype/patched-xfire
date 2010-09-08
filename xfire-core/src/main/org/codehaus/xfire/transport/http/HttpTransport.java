package org.codehaus.xfire.transport.http;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.DefaultEndpoint;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class HttpTransport
    extends AbstractTransport 
{
    private static final Log log = LogFactory.getLog(HttpTransport.class);

    public final static String HTTP_BINDING = "http://www.w3.org/2004/08/wsdl/http";
    
    public final static String CHUNKING_ENABLED = "urn:xfire:transport:http:chunking-enabled";
    
    public final static String HTTP_TRANSPORT_NS = "http://schemas.xmlsoap.org/soap/http";

    private final static String URI_PREFIX = "urn:xfire:transport:http:";
    
    public HttpTransport()
    {
        
    }

    protected Channel createNewChannel(String uri)
    {
        log.debug("Creating new channel for uri: " + uri);
        
        HttpChannel c = new HttpChannel(uri, this);
        c.setEndpoint(new DefaultEndpoint());

        return c;
    }

    protected String getUriPrefix()
    {
        return URI_PREFIX;
    }

    /**
	 * Get the URL for a particular service.
	 */
	public String getServiceURL( Service service )
	{
        return "http://localhost/services/" + service.getSimpleName();
    }
    
    public String getTransportURI( Service service )
    {
        return HTTP_TRANSPORT_NS;
    }
    
    public String[] getSupportedBindings()
    {
        return new String[] { HTTP_BINDING };
    }

    public String[] getKnownUriSchemes()
    {
        return new String[] { "http://", "https://" };
    }
}