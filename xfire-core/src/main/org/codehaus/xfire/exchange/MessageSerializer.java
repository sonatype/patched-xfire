package org.codehaus.xfire.exchange;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

public interface MessageSerializer
{
    void readMessage(InMessage message, MessageContext context)
        throws XFireFault;

    void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault;
}
