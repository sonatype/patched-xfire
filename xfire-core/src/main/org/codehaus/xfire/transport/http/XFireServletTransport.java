/**
 * 
 */
package org.codehaus.xfire.transport.http;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.DefaultEndpoint;

final class XFireServletTransport
    extends SoapHttpTransport
{
    protected Channel createNewChannel(String uri)
    {
        XFireServletChannel c = new XFireServletChannel(uri, this);
        c.setEndpoint(new DefaultEndpoint());

        return c;
    }

    public String getServiceURL(Service service)
    {
        HttpServletRequest req = XFireServletController.getRequest();
    
        if (req == null) return super.getServiceURL(service);
        
        StringBuffer output = new StringBuffer( 128 );
    
        output.append( req.getScheme() );
        output.append( "://" );
        output.append( req.getServerName() );
    
        if ( req.getServerPort() != 80 &&
             req.getServerPort() != 443 &&
             req.getServerPort() != 0 )
        {
            output.append( ':' );
            output.append( req.getServerPort() );
        }
    
        output.append( req.getRequestURI() );
    
        return output.toString();
    }
}