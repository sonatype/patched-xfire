package org.codehaus.xfire.util.jdom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.jdom.Document;

/**
 * Reads/writes JDOM documents to the body of a message.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class JDOMSerializer 
    implements MessageSerializer
{
    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        StaxBuilder builder = new StaxBuilder();
        try
        {
            Document doc = builder.build(message.getXMLStreamReader());
            message.setBody(doc);
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't parse message.", e, XFireFault.SENDER);
        }
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        Document doc = (Document) message.getBody();
        StaxSerializer serializer = new StaxSerializer();
        
        try
        {
            serializer.writeElement(doc.getRootElement(), writer);
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Couldn't write message.", e, XFireFault.RECEIVER);
        }
    }
    
}