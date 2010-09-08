package org.codehaus.xfire.wsdl;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes a WSDL file for a service. This is WSDL version agnostic.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WSDLWriter
{
    public static final String WSDL11_NS = "http://schemas.xmlsoap.org/wsdl/";
    
    public static final String WSDL11_SOAP_NS = "http://schemas.xmlsoap.org/wsdl/soap/";

    /**
     * Write the WSDL to an OutputStream.
     * 
     * @param out The OutputStream.
     * @throws IOException
     */
    void write(OutputStream out) throws IOException;
}