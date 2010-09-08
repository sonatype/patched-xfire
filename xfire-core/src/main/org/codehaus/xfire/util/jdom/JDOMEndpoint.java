package org.codehaus.xfire.util.jdom;

import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.transport.ChannelEndpoint;
import org.codehaus.xfire.util.stax.FragmentStreamReader;
import org.jdom.Document;

public class JDOMEndpoint
    implements ChannelEndpoint
{
    private int count = 0;
    private Document message;
    
    public void onReceive(MessageContext context, InMessage msg)
    {
        StaxBuilder builder = new StaxBuilder();
        try
        {
            message = builder.build(new FragmentStreamReader(msg.getXMLStreamReader()));
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
        count++;
    }

    public int getCount()
    {
        return count;
    }

    public Document getMessage()
    {
        return message;
    }
}