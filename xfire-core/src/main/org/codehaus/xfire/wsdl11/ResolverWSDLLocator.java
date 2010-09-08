package org.codehaus.xfire.wsdl11;

import java.io.IOException;
import java.io.InputStream;

import javax.wsdl.xml.WSDLLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.util.Resolver;
import org.xml.sax.InputSource;

/**
 * A WSDL resource locator.
 * 
 * @author <a href="mailto:dlaprade@gmail.com">Daniel LaPrade</a>
 * 
 */
public class ResolverWSDLLocator
    implements WSDLLocator
{

    private static final Log LOG = LogFactory.getLog(ResolverWSDLLocator.class.getName());

    private String baseURI;

    private String lastimport = "";

    private InputSource inputsource = null;

    public ResolverWSDLLocator(String baseURI, InputSource inputsource)
    {
        this.inputsource = inputsource;
        this.baseURI = baseURI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.wsdl.xml.WSDLLocator#getBaseInputSource()
     */
    public InputSource getBaseInputSource()
    {
        return inputsource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.wsdl.xml.WSDLLocator#getBaseURI()
     */
    public String getBaseURI()
    {
        return baseURI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.wsdl.xml.WSDLLocator#getImportInputSource(java.lang.String,
     *      java.lang.String)
     */
    public InputSource getImportInputSource(String arg0, String name)
    {
        Resolver resolver;
        InputSource result = null;

        // Set the last imported value.
        lastimport = name;
        try
        {
            resolver = new Resolver(baseURI, name);
            InputStream is = resolver.getInputStream();
            if (is != null)
                result = new InputSource(resolver.getInputStream());
        }
        catch (IOException e)
        {
            LOG.warn("Source: " + name + " failed to find input source with exception: ", e);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.wsdl.xml.WSDLLocator#getLatestImportURI()
     */
    public String getLatestImportURI()
    {
        return lastimport;
    }

    public void close() { }
}