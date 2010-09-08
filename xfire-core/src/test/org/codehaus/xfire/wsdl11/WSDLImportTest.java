package org.codehaus.xfire.wsdl11;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

public class WSDLImportTest
    extends TestCase
{
    public void testImports() throws Exception
    {
    }
    public void xtestImports() throws Exception
    {
        Definition d = WSDLFactory.newInstance().newWSDLReader().readWSDL(
            new ResolverWSDLLocator(null, new InputSource(getClass().getResourceAsStream("echoImport.wsdl"))));
        
        assertNotNull(d);
    }
}
