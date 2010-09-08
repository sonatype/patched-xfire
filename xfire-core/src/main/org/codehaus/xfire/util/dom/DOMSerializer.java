package org.codehaus.xfire.util.dom;

import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.soap.SoapSerializer;
import org.codehaus.xfire.util.STAXUtils;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 */
public class DOMSerializer
    implements MessageSerializer
{
    private static final Log LOG = LogFactory.getLog(DOMSerializer.class);

    public DOMSerializer()
    {
    }

    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        throw new UnsupportedOperationException();
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        try
        {
            Document doc = (Document) message.getProperty(DOMOutHandler.DOM_MESSAGE);
            STAXUtils.writeDocument(doc, writer, Boolean.TRUE.equals(context.getProperty(SoapSerializer.SERIALIZE_PROLOG)), false);

            writer.flush();
        }
        catch (Exception e)
        {
            LOG.error(e);
            throw XFireFault.createFault(e);
        }
    }
}
