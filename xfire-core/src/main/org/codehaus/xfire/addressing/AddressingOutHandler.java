package org.codehaus.xfire.addressing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.OperationInfo;

public class AddressingOutHandler
    extends AbstractHandler
{
    private final static Log logger = LogFactory.getLog(AddressingOutHandler.class);
    
    public AddressingOutHandler()
    {
        super();
        setPhase(Phase.POST_INVOKE);
    }

    public void invoke(MessageContext context)
        throws Exception
    {
    	if(Boolean.TRUE.equals(context.getProperty(Client.CLIENT_MODE)))
        {
        	invokeClient(context);
    	}
    	else
        {
        	invokeServer(context);
    	}
    }

    private void invokeServer(MessageContext context)
    {
        OutMessage msg = (OutMessage) context.getCurrentMessage();
        AddressingHeaders headers = (AddressingHeaders) msg.getProperty(AddressingInHandler.ADRESSING_HEADERS);
        AddressingHeadersFactory factory = (AddressingHeadersFactory) msg.getProperty(AddressingInHandler.ADRESSING_FACTORY);
        
        if (headers == null)
        {
            logger.debug("Couldn't find addressing headers.");
            return;
        }
        
        if (msg == null)
        {
            logger.warn("There was no out message!");
            return;
        }

        factory.writeHeaders(msg.getOrCreateHeader(), headers);
    }

    private void invokeClient(MessageContext context)
    {
        OperationInfo oi=context.getExchange().getOperation();
        AddressingOperationInfo aoi = (AddressingOperationInfo)
            oi.getProperty(AddressingOperationInfo.ADDRESSING_OPERATION_KEY);

        if (aoi == null) return;
        
        AddressingHeadersFactory factory = (AddressingHeadersFactory) oi.getProperty(AddressingInHandler.ADRESSING_FACTORY.toString());
        if (factory==null) factory = new AddressingHeadersFactory200508();
        
        AddressingHeaders headers = new AddressingHeaders();
        headers.setTo(aoi.getTo());
        headers.setAction(aoi.getInAction());
        headers.setReplyTo(aoi.getReplyTo());
        headers.setFaultTo(aoi.getFaultTo());
        headers.setFrom(aoi.getFrom());
        headers.setMessageID("urn:uuid:" + new RandomGUID(false).toString());
        context.setId(headers.getMessageID());
        
        OutMessage msg = (OutMessage) context.getCurrentMessage();

        if (msg == null)
        {
            logger.warn("There was no out message!");
        }
        else
        {
            factory.writeHeaders(msg.getOrCreateHeader(), headers);
        }
    }
    
}