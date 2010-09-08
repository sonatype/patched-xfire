package org.codehaus.xfire.addressing;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.AbstractHandler;

public class WSATestHandler
    extends AbstractHandler
{

    private AddressingInData data;

    public WSATestHandler(AddressingInData data)
    {
        super();
        this.data = data;
    }

    public void invoke(MessageContext context)
        throws Exception
    {

        AddressingHeaders inHeaders = (AddressingHeaders) context.getInMessage()
                .getProperty(AddressingInHandler.ADRESSING_HEADERS);
        data.setInHeaders(inHeaders);
        OutMessage msg = context.getOutMessage();
        AddressingHeaders headers = (AddressingHeaders) msg
                .getProperty(AddressingInHandler.ADRESSING_HEADERS);
        data.setOutHeaders(headers);

    }

}
