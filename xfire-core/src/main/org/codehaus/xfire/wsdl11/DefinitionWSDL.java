package org.codehaus.xfire.wsdl11;

import java.io.IOException;
import java.io.OutputStream;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.wsdl.WSDLWriter;

public class DefinitionWSDL
    implements WSDLWriter
{
    private Definition def;
    
    public DefinitionWSDL(Definition def)
    {
        this.def = def;
    }

    public void write(OutputStream out)
        throws IOException
    {
        try
        {
            javax.wsdl.xml.WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
            writer.writeWSDL(def, out);
        }
        catch (WSDLException e)
        {
            throw new XFireRuntimeException("Could not write wsdl definition!", e);
        }
    }
}
