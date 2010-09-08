package org.codehaus.xfire.exchange;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.dead.DeadLetterTransport;

/**
 * A MessageExchange encapsulates the orchestration of a message
 * exchange pattern.  This makes it easy to handle various interactions -
 * like robust in-out, robust in, in, out, WS-Addressing MEPs, etc.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class MessageExchange
{
    private OperationInfo operation;

    private MessageContext context;
    
    private InMessage inMessage;
    private OutMessage outMessage;
    private AbstractMessage faultMessage;
    
    private boolean hasOutput = false;
    private boolean hasInput = true;
    private boolean hasFault = true;
    
    public MessageExchange(MessageContext context)
    {
        this.context = context;
        
        if (context.getExchange() != null)
        {
            setInMessage(context.getExchange().getInMessage());
        }
        
        context.setExchange(this);
    }
    
    public MessageContext getContext()
    {
        return context;
    }

    public OperationInfo getOperation()
    {
        return operation;
    }

    public void setOperation(OperationInfo operation)
    {
        this.operation = operation;
        
        hasOutput = operation.hasOutput();
        hasInput = operation.hasInput();
    }

    public InMessage getInMessage()
        throws UnsupportedOperationException
    {
        return inMessage;
    }

    public OutMessage getOutMessage()
    {
        if (outMessage == null && hasOutMessage())
        {
            outMessage = new OutMessage(Channel.BACKCHANNEL_URI);
            outMessage.setChannel(getOutChannel());
            outMessage.setSoapVersion(getInMessage().getSoapVersion());
    
            setOutMessage(outMessage);
        }

        return outMessage;
    }

    public AbstractMessage getFaultMessage()
        throws UnsupportedOperationException
    {
        if (faultMessage == null && hasFaultMessage())
        {
            faultMessage = new OutMessage(Channel.BACKCHANNEL_URI);
            faultMessage.setChannel(getFaultChannel());
            faultMessage.setSoapVersion(getInMessage().getSoapVersion());
    
            setFaultMessage(faultMessage);
        }
        return faultMessage;
    }
    

    public void setFaultMessage(AbstractMessage faultMessage)
    {
        this.faultMessage = faultMessage;
    }

    public void setInMessage(InMessage inMessage)
    {
        this.inMessage = inMessage;
    }

    public void setOutMessage(OutMessage outMessage)
    {
        this.outMessage = outMessage;
    }

    public boolean hasFaultMessage()
    {
        return hasFault;
    }

    public boolean hasInMessage()
    {
        return hasInput;
    }

    public boolean hasOutMessage()
    {
        return hasOutput;
    }

    public Channel getInChannel()
    {
        if (hasInMessage())
        {
            return getInMessage().getChannel();
        }
        else
        {
            return getDeadLetterChannel();
        }
    }

    public Channel getOutChannel()
    {
        if (hasOutMessage())
        {
            return getInMessage().getChannel();
        }
        else
        {
            return getDeadLetterChannel();
        }
    }

    public Channel getFaultChannel()
    {
        if (hasFaultMessage())
        {
            return getInMessage().getChannel();
        }
        else
        {
            return getDeadLetterChannel();
        }
    }
    
    public Channel getDeadLetterChannel()
    {
        TransportManager tm = getContext().getXFire().getTransportManager();
        Transport transport = tm.getTransport(DeadLetterTransport.NAME);
        
        try
        {
            return transport.createChannel();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            return null;
        }
    }

    public void setHasFault(boolean hasFault)
    {
        this.hasFault = hasFault;
    }

    public void setHasInput(boolean hasInput)
    {
        this.hasInput = hasInput;
    }

    public void setHasOutput(boolean hasOutput)
    {
        this.hasOutput = hasOutput;
    }
    
    
}
