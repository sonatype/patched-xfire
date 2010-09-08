package org.codehaus.xfire.fault;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.STAXUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class FaultSerializerTest
        extends AbstractXFireTest
{
    public void testFaults()
            throws Exception
    {
        Soap12FaultSerializer soap12 = new Soap12FaultSerializer();

        XFireFault fault = new XFireFault(new Exception());
        fault.setRole("http://someuri");
        fault.setSubCode(new QName("urn:test", "NotAvailable", "m"));
        Element e = new Element("bah", "t", "urn:test");
        e.addContent("bleh");
        fault.getDetail().addContent(e);

        e = new Element("bah2", "t", "urn:test2");
        e.addContent("bleh");
        fault.getDetail().addContent(e);

        fault.addNamespace("m", "urn:test");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        OutMessage message = new OutMessage("urn:bleh");
        message.setBody(fault);
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, "UTF-8",null);
        writer.writeStartDocument();
        writer.writeStartElement("soap", "Body", Soap12.getInstance().getNamespace());
        writer.setPrefix("soap", Soap12.getInstance().getNamespace());
        writer.writeNamespace("soap", Soap12.getInstance().getNamespace());
        soap12.writeMessage(message, writer, new MessageContext());
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
        
        Document doc = readDocument(out.toString());
        //printNode(doc);
        addNamespace("s", Soap12.getInstance().getNamespace());
        assertValid("//s:SubCode/s:Value[text()='m:NotAvailable']", doc);
        addNamespace("t", "urn:test2");
        assertValid("//s:Detail/t:bah2[text()='bleh']", doc);
        assertValid("//s:Role[text()='http://someuri']", doc);
        
        XMLStreamReader reader = readerForString(out.toString());
        InMessage inMsg = new InMessage(reader);
        
        while (reader.hasNext())
        {
            reader.next();
            
            if (reader.getEventType() == XMLStreamReader.START_ELEMENT 
                    && reader.getLocalName().equals("Fault"))
            {
                break;
            }
        }
        
        soap12.readMessage(inMsg, new MessageContext());
        
        assertNotNull(inMsg.getBody());
        assertTrue(inMsg.getBody() instanceof XFireFault);
        XFireFault fault2 = (XFireFault) inMsg.getBody();
        
        assertEquals(fault.getMessage(), fault2.getMessage());
        assertEquals(fault.getSubCode(), fault2.getSubCode());
        assertEquals(fault.getFaultCode(), fault2.getFaultCode());
        
        assertNotNull(fault.getDetail().getChild("bah2", Namespace.getNamespace("urn:test2")));
    }

    public void testFaults11()
            throws Exception
    {
        Soap11FaultSerializer soap11 = new Soap11FaultSerializer();

        XFireFault fault = new XFireFault(new Exception());
        fault.setRole("http://someuri");
        
        Element e = new Element("bah", "t", "urn:test");
        e.addContent("bleh");
        fault.getDetail().addContent(e);

        e = new Element("bah2", "t", "urn:test2");
        e.addContent("bleh");
        fault.getDetail().addContent(e);

        fault.addNamespace("m", "urn:test");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        OutMessage message = new OutMessage("urn:bleh");
        message.setBody(fault);
        
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, "UTF-8",null);
        writer.writeStartDocument();
        writer.writeStartElement("soap", "Body", Soap11.getInstance().getNamespace());
        writer.writeNamespace("soap", Soap11.getInstance().getNamespace());
        soap11.writeMessage(message, writer, new MessageContext());
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();

        Document doc = readDocument(out.toString());

        addNamespace("s", Soap12.getInstance().getNamespace());
        addNamespace("t", "urn:test2");
        assertValid("//detail/t:bah2[text()='bleh']", doc);
        assertValid("//faultactor[text()='http://someuri']", doc);
        
        XMLStreamReader reader = readerForString(out.toString());
        InMessage inMsg = new InMessage(reader);
        
        soap11.readMessage(inMsg, new MessageContext());
        
        assertNotNull(inMsg.getBody());
        assertTrue(inMsg.getBody() instanceof XFireFault);
        XFireFault fault2 = (XFireFault) inMsg.getBody();
        
        assertEquals(XFireFault.SOAP11_SERVER, fault2.getFaultCode());
        assertEquals(fault.getMessage(), fault2.getMessage());
        
        assertNotNull(fault.getDetail().getChild("bah2", Namespace.getNamespace("urn:test2")));
    }


    public void testCustomPrefix()
            throws Exception
    {
        Soap11FaultSerializer soap11 = new Soap11FaultSerializer();

        XFireFault fault = new XFireFault(new Exception());
        fault.setFaultCode(new QName("http:///test-uri", "test", "t"));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        OutMessage message = new OutMessage("urn:bleh");
        message.setBody(fault);
        
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, "UTF-8",null);
        writer.writeStartDocument();
        writer.writeStartElement("soap", "Body", Soap11.getInstance().getNamespace());
        writer.writeNamespace("soap", Soap11.getInstance().getNamespace());
        soap11.writeMessage(message, writer, new MessageContext());
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();

        Document doc = readDocument(out.toString());

        addNamespace("s", Soap12.getInstance().getNamespace());
        addNamespace("t", "urn:test2");
        assertValid("//faultcode[text()='t:test']", doc);
    }

    public void testCustomPrefixSoap12()
            throws Exception
    {
        Soap12FaultSerializer soap11 = new Soap12FaultSerializer();

        XFireFault fault = new XFireFault(new Exception());
        fault.setFaultCode(new QName("http:///test-uri", "test", "t"));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        OutMessage message = new OutMessage("urn:bleh");
        message.setBody(fault);
        
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, "UTF-8",null);
        writer.writeStartDocument();
        writer.writeStartElement("soap", "Body", Soap12.getInstance().getNamespace());
        writer.writeNamespace("soap", Soap12.getInstance().getNamespace());
        soap11.writeMessage(message, writer, new MessageContext());
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();

        Document doc = readDocument(out.toString());

        addNamespace("s", Soap12.getInstance().getNamespace());
        addNamespace("t", "urn:test2");
        assertValid("//s:Code/s:Value[text()='t:test']", doc);
    }


    
    private XMLStreamReader readerForString(String string) throws XMLStreamException
    {
        XMLInputFactory factory = STAXUtils.getXMLInputFactory(null);
        return factory.createXMLStreamReader(new StringReader(string));
    }
}
