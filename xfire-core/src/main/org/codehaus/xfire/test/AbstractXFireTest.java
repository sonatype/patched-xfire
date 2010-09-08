package org.codehaus.xfire.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageExchange;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.xfire.transport.MapSession;
import org.codehaus.xfire.transport.Session;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Contains helpful methods to test SOAP services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractXFireTest
    extends TestCase
{
    private ServiceFactory factory;

    private static String basedirPath;

    private XMLInputFactory defaultInputFactory = XMLInputFactory.newInstance();

    /**
     * Namespaces for the XPath expressions.
     */
    private Map namespaces = new HashMap();

    private MapSession session;

    private XFire xfire;

    protected void printNode(Document node)
        throws Exception
    {
        XMLOutputter writer = new XMLOutputter(Format.getPrettyFormat());

        writer.output(node, System.out);
    }

    protected void printNode(Element node)
        throws Exception
    {
        XMLOutputter writer = new XMLOutputter(Format.getPrettyFormat());

        writer.output(node, System.out);
    }

    protected String stringNode(Document node)
        throws Exception
    {
        XMLOutputter writer = new XMLOutputter(Format.getPrettyFormat());
        StringWriter sw = new StringWriter();
        writer.output(node, sw);
        return sw.toString();
    }

    protected String stringNode(Element node)
        throws Exception
    {
        XMLOutputter writer = new XMLOutputter(Format.getPrettyFormat());
        StringWriter sw = new StringWriter();
        writer.output(node, sw);
        return sw.toString();
    }

    /**
     * Invoke a service with the specified document.
     * 
     * @param service
     *            The name of the service.
     * @param document
     *            The request as an xml document in the classpath.
     */
    protected Document invokeService(String service, String document)
        throws Exception
    {
        InputStream stream = getResourceAsStream(document);
        return invokeService(service, STAXUtils.createXMLStreamReader(stream, "UTF-8", null));
    }

    /**
     * Invoke a service with the specified document.
     * 
     * @param service
     *            The name of the service.
     * @param streamReader
     *            Stream representing incoming message
     */
    protected Document invokeService(String service, XMLStreamReader streamReader)
        throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = new MessageContext();
        context.setSession(session);
        context.setXFire(getXFire());
        context.setProperty(Channel.BACKCHANNEL_URI, out);

        if (service != null)
            context.setService(getServiceRegistry().getService(service));

        InMessage msg = new InMessage(streamReader);

        Transport t = getXFire().getTransportManager().getTransport(LocalTransport.BINDING_ID);
        Channel c = t.createChannel();

        c.receive(context, msg);

        String response = out.toString();
        if (response == null || response.length() == 0)
            return null;

        return readDocument(response);
    }

    public static final String RESPONSE = "response";

    protected InMessage invokeService(OutMessage msg, String transportID)
        throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = new MessageContext();
        context.setXFire(getXFire());
        context.setProperty(Channel.BACKCHANNEL_URI, out);

        Transport t = getTransportManager().getTransport(transportID);
        Channel c = t.createChannel();
        msg.setChannel(c);

        msg.setSerializer(new CopySerializer());

        c.setEndpoint(new ChannelEndpoint()
        {
            public void onReceive(MessageContext context, InMessage msg)
            {
                try
                {
                    // force attachment read...
                    if (msg.getAttachments() != null)
                        msg.getAttachments().getParts();

                    Document doc = new StaxBuilder().build(msg.getXMLStreamReader());
                    context.setExchange(new MessageExchange(context));
                    context.getExchange().setInMessage(msg);

                    msg.setProperty(RESPONSE, doc);
                }
                catch (XMLStreamException e)
                {
                    e.printStackTrace();
                }
            }
        });

        c.send(context, msg);

        return context.getInMessage();
    }

    protected Document readDocument(String text)
        throws XMLStreamException
    {
        return readDocument(text, defaultInputFactory);
    }

    protected Document readDocument(String text, XMLInputFactory ifactory)
        throws XMLStreamException
    {
        try
        {
            StaxBuilder builder = new StaxBuilder(ifactory);
            return builder.build(new StringReader(text));
        }
        catch (XMLStreamException e)
        {
            System.err.println("Could not read the document!");
            System.err.println(text);
            throw e;
        }
    }

    protected Document getWSDLDocument(String service)
        throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        getXFire().generateWSDL(service, out);

        return readDocument(out.toString());
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        System
                .setProperty("javax.xml.stream.XMLInputFactory",
                             "com.ctc.wstx.stax.WstxInputFactory");
        addNamespace("s", Soap11.getInstance().getNamespace());
        addNamespace("soap12", Soap12.getInstance().getNamespace());
        addNamespace("wsdl", WSDLBuilder.WSDL11_NS);
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("xsi", SoapConstants.XSI_NS);
        createSession();
    }

    protected void tearDown()
        throws Exception
    {
        factory = null;
        xfire = null;
        session = null;
        namespaces = null;
        basedirPath = null;

        XFireFactory.setInstance(null);
        super.tearDown();
    }

    protected void createSession()
    {
        session = new MapSession();
    }

    /**
     * Assert that the following XPath query selects one or more nodes.
     * 
     * @param xpath
     */
    public List assertValid(String xpath, Object node)
        throws Exception
    {
        return XPathAssert.assertValid(xpath, node, namespaces);
    }

    /**
     * Assert that the following XPath query selects no nodes.
     * 
     * @param xpath
     */
    public List assertInvalid(String xpath, Object node)
        throws Exception
    {
        return XPathAssert.assertInvalid(xpath, node, namespaces);
    }

    /**
     * Asser that the text of the xpath node retrieved is equal to the value
     * specified.
     * 
     * @param xpath
     * @param value
     * @param node
     */
    public void assertXPathEquals(String xpath, String value, Document node)
        throws Exception
    {
        XPathAssert.assertXPathEquals(xpath, value, node, namespaces);
    }

    public void assertNoFault(Document node)
        throws Exception
    {
        XPathAssert.assertNoFault(node);
    }

    /**
     * Add a namespace that will be used for XPath expressions.
     * 
     * @param ns
     *            Namespace name.
     * @param uri
     *            The namespace uri.
     */
    public void addNamespace(String ns, String uri)
    {
        namespaces.put(ns, uri);
    }

    /**
     * Get the WSDL for a service.
     * 
     * @param service
     *            The name of the service.
     */
    protected WSDLWriter getWSDL(String service)
        throws Exception
    {
        ServiceRegistry reg = getServiceRegistry();
        Service hello = reg.getService(service);

        return hello.getWSDLWriter();
    }

    protected Session getSession()
    {
        return session;
    }

    protected XFire getXFire()
    {
        if (xfire == null)
            xfire = new DefaultXFire();

        return xfire;
    }

    protected TransportManager getTransportManager()
    {
        return getXFire().getTransportManager();
    }

    protected ServiceRegistry getServiceRegistry()
    {
        return getXFire().getServiceRegistry();
    }

    public ServiceFactory getServiceFactory()
    {
        if (factory == null)
        {
            ObjectServiceFactory ofactory = new ObjectServiceFactory(getTransportManager(),
                    new MessageBindingProvider());

            ofactory.setStyle(SoapConstants.STYLE_MESSAGE);

            factory = ofactory;
        }

        return factory;
    }

    public void setServiceFactory(ServiceFactory factory)
    {
        this.factory = factory;
    }

    protected XFireProxyFactory getXFireProxyFactory()
    {
        return new XFireProxyFactory(getXFire());
    }

    protected InputStream getResourceAsStream(String resource)
    {
        return getClass().getResourceAsStream(resource);
    }

    protected Reader getResourceAsReader(String resource)
    {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    public File getTestFile(String relativePath)
    {
        return new File(getBasedir(), relativePath);
    }

    public static String getBasedir()
    {
        if (basedirPath != null)
        {
            return basedirPath;
        }

        basedirPath = System.getProperty("basedir");

        if (basedirPath == null)
        {
            basedirPath = new File("").getAbsolutePath();
        }

        return basedirPath;
    }

    protected static class CopySerializer
        implements MessageSerializer
    {
        public CopySerializer()
        {
            super();
        }

        public void readMessage(InMessage arg0, MessageContext arg1)
            throws XFireFault
        {
        }

        public void writeMessage(OutMessage out, XMLStreamWriter writer, MessageContext ctx)
            throws XFireFault
        {
            try
            {
                STAXUtils.copy((XMLStreamReader) out.getBody(), writer);
            }
            catch (XMLStreamException e)
            {
                throw new XFireFault("Couldn't write to stream.", e, XFireFault.RECEIVER);
            }
        }
    }
}