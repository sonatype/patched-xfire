package org.codehaus.xfire.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Channel;

/**
 * Responsible for taking an exception, turning it into a Fault, then sending
 * (and logging) that fault to the appropriate location.
 * 
 * @author Dan Diephouse
 */
public class DefaultFaultHandler extends AbstractHandler
{
    private static final Log log = LogFactory.getLog(DefaultFaultHandler.class);
    
    public static final String EXCEPTION = "exception";

    public void invoke(MessageContext context)
        throws Exception
    {
        Throwable e = (Throwable) context.getProperty(EXCEPTION);

        XFireFault fault = XFireFault.createFault(e);
        
        // get the root cause so we know what level to log
        if (fault.getCause() != null) e = fault.getCause();
        
        if (e instanceof RuntimeException)
        {
            log.error("Fault occurred!", e);
        }
        else if (log.isInfoEnabled())
        {
            log.info("Fault occurred!", e);
        }
        
        context.setCurrentMessage(context.getExchange().getFaultMessage());

        Service service = context.getService();
        if (service == null || service.getFaultSerializer() == null || !context.getExchange().hasFaultMessage())
        {
            sendToDeadLetter(fault, context);
        }
        else
        {
            sendFault(fault, context);
        }
    }

    protected void sendToDeadLetter(XFireFault fault, MessageContext context)
    {
        log.error("Could not find service.", fault);
    }

    protected void sendFault(XFireFault fault, MessageContext context)
    {
        // Create the outgoing fault message
        OutMessage outMsg = (OutMessage) context.getExchange().getFaultMessage();
        
        outMsg.setSerializer(context.getService().getFaultSerializer());
        outMsg.setBody(fault);
        
        context.setCurrentMessage(outMsg);
        
        // Create a fault pipeline
        HandlerPipeline faultPipe = new HandlerPipeline(context.getXFire().getOutPhases());
        
        faultPipe.addHandlers(context.getXFire().getFaultHandlers());
        
        Channel faultChannel = context.getExchange().getFaultMessage().getChannel();
        if (faultChannel != null)
        {
            faultPipe.addHandlers(faultChannel.getTransport().getFaultHandlers());
        }

        if (context.getService() != null)
        {
            faultPipe.addHandlers(context.getService().getFaultHandlers());
        }
        
        try
        {
            faultPipe.invoke(context);
        }
        catch (Exception e1)
        {
            // An exception occurred while sending the fault. Log and move on.
            XFireFault fault2 = XFireFault.createFault(e1);
            faultPipe.handleFault(fault2, context);
            
            log.error("Could not send fault.", e1);
        }
    }

}
