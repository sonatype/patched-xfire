package org.codehaus.xfire.wsdl11;


import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Transport;

/**
 * Indicates that a particular transport supports WSDL 1.1 generation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WSDL11Transport extends Transport
{
    public String getName();

    public String getServiceURL(Service service);
}
