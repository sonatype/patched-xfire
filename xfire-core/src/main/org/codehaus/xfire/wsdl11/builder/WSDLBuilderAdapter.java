package org.codehaus.xfire.wsdl11.builder;

import java.io.IOException;
import java.io.OutputStream;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * An adapter for a <code>WSDLBuilder</code> so that it conforms to the <code>WSDLWriter</code> interface.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class WSDLBuilderAdapter
        implements WSDLWriter
{
    private WSDLBuilderFactory wsdlBuilderFactory;
    private Service service;
    private TransportManager transportManager;
    
    public WSDLBuilderAdapter(WSDLBuilderFactory wsdlBuilderFactory,
                              Service service, 
                              TransportManager transportManager)
    {
        this.wsdlBuilderFactory = wsdlBuilderFactory;
        this.service = service;
        this.transportManager = transportManager;
    }

    /**
     * Write the WSDL to an OutputStream.
     *
     * @param out The OutputStream.
     * @throws java.io.IOException
     */
    public void write(OutputStream out)
        throws IOException
    {
        wsdlBuilderFactory.createWSDLBuilder(service, transportManager).write(out);
    }
}
