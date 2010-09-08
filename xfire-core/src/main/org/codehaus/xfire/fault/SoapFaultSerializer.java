package org.codehaus.xfire.fault;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;

/**
 * Inspects the soap version and chooses an appropriate fault serializer.
 * 
 * @author Dan
 *
 */
public class SoapFaultSerializer
    implements MessageSerializer
{
    private static Soap11FaultSerializer soap11 = new Soap11FaultSerializer();
    private static Soap12FaultSerializer soap12 = new Soap12FaultSerializer();
    
    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        if (message.getSoapVersion() instanceof Soap11)
            soap11.readMessage(message, context);
        else if (message.getSoapVersion() instanceof Soap12)
            soap12.readMessage(message, context);
        else 
            throw new XFireFault("Unrecognized soap version.", 
                                 (XFireFault) message.getBody(),
                                 XFireFault.SENDER);
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        if (message.getSoapVersion() instanceof Soap11)
            soap11.writeMessage(message, writer, context);
        else if (message.getSoapVersion() instanceof Soap12)
            soap12.writeMessage(message, writer, context);
        else 
            throw new XFireFault("Unrecognized soap version.", 
                                 (XFireFault) message.getBody(),
                                 XFireFault.SENDER);
    }

}
