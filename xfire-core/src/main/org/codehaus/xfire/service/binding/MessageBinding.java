package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.stax.DepthXMLStreamReader;

/**
 * Handles messages.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Feb 18, 2004
 */
public class MessageBinding
    extends AbstractBinding
{
    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        final Service service = context.getService();
        
        OperationInfo operation = context.getExchange().getOperation();

        if (context.getExchange().getOperation() == null)
        {
            operation = (OperationInfo) service.getServiceInfo().getOperations().iterator().next();
            
            setOperation(operation, context);
        }

        DepthXMLStreamReader dr = new DepthXMLStreamReader(message.getXMLStreamReader());

        final List params = new ArrayList();
        message.setBody( params );

        MessageInfo msg;
        if (context.getClient() != null)
        {
            msg = operation.getOutputMessage();
        }
        else
        {
            msg = operation.getInputMessage();
        }
        
        if (!STAXUtils.toNextElement(dr))
        {
            return;
        }

        Binding binding = context.getBinding();
        for (Iterator itr = msg.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo p = (MessagePartInfo) itr.next();

            params.add( service.getBindingProvider().readParameter(p, message.getXMLStreamReader(), context) );

            nextEvent(message.getXMLStreamReader());
        }
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        Object[] values = (Object[]) message.getBody();
        final OperationInfo operation = context.getExchange().getOperation();
        
        int i = 0;
        MessageInfo msg;
        if (context.getClient() != null)
        {
            msg = operation.getInputMessage();
        }
        else
        {
            msg = operation.getOutputMessage();
        }
        
        for (Iterator itr = msg.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo p = (MessagePartInfo) itr.next();
 
            context.getService().getBindingProvider().writeParameter(p, writer, context, values[i]);
            i++;
        }
    }   
}
