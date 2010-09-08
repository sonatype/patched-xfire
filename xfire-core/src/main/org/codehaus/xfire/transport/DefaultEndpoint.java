package org.codehaus.xfire.transport;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageExchange;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandlerSupport;
import org.codehaus.xfire.handler.DefaultFaultHandler;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;

/**
 * A <code>ChannelEndpoint</code> which executes the in pipeline
 * on the service and starts a <code>MessageExchange</code>.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DefaultEndpoint
    extends AbstractHandlerSupport
    implements ChannelEndpoint
{
    private static final Log log = LogFactory.getLog(DefaultEndpoint.class);
    public static final String SERVICE_HANDLERS_REGISTERED = "service.handlers.registered";

    public DefaultEndpoint()
    {
    }
    
    public void onReceive(MessageContext context, InMessage msg)
    {
        if (log.isDebugEnabled()) log.debug("Received message to " + msg.getUri());
        
        if (context.getExchange() == null)
        {
            MessageExchange exchange = new MessageExchange(context);
            exchange.setInMessage(msg);
            context.setCurrentMessage(msg);
        }
        
        // Create the handlerpipeline and invoke it
        HandlerPipeline pipeline = new HandlerPipeline(context.getXFire().getInPhases());
        pipeline.addHandlers(context.getXFire().getInHandlers());
        pipeline.addHandlers(msg.getChannel().getTransport().getInHandlers());
        pipeline.addHandlers(getInHandlers());
        
        if (context.getService() != null)
        {
            pipeline.addHandlers(context.getService().getInHandlers());
            context.setProperty(SERVICE_HANDLERS_REGISTERED, Boolean.TRUE);
        }
        
        context.setInPipeline(pipeline);
        
        if (context.getFaultHandler() == null)
            context.setFaultHandler(createFaultHandler());
        
        try
        {
            pipeline.invoke(context);
            
            // finishReadingMessage(msg, context);
        }
        catch (Exception e)
        {
            XFireFault fault = XFireFault.createFault(e);
            context.setProperty(DefaultFaultHandler.EXCEPTION, fault);

            pipeline.handleFault(fault, context);
            
            try
            {
                context.getFaultHandler().invoke(context);
            }
            catch (Exception e1)
            {
                log.warn("Error invoking fault handler.", e1);
            }
        }
    }

    protected Handler createFaultHandler()
    {
        return new DefaultFaultHandler();
    }

    public void finishReadingMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        XMLStreamReader reader = message.getXMLStreamReader();

        try
        {
            int event = reader.getEventType();
            while (event != XMLStreamReader.END_DOCUMENT && reader.hasNext()) 
                event = reader.next();
        }
        catch (XMLStreamException e)
        {
            log.warn("Couldn't parse to end of message.", e);
        }
    }
}
