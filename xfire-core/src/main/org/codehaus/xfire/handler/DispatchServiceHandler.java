package org.codehaus.xfire.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.soap.handler.SoapBodyHandler;
import org.codehaus.xfire.transport.DefaultEndpoint;

/**
 * Reads in the message body using the service binding.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DispatchServiceHandler
    extends AbstractHandler
{
    public DispatchServiceHandler()
    {
        super();
        setPhase(Phase.DISPATCH);
        after(SoapBodyHandler.class.getName());
    }

    public void invoke(MessageContext context)
        throws XFireFault
    {
        Boolean b = (Boolean) context.getProperty(DefaultEndpoint.SERVICE_HANDLERS_REGISTERED);
        if ((b == null || b.equals(Boolean.FALSE)) && context.getService() != null)
        {
            context.getInPipeline().addHandlers(context.getService().getInHandlers());
        }
        
        if (context.getExchange().hasOutMessage())
        {
            HandlerPipeline pipeline = new HandlerPipeline(context.getXFire().getOutPhases());
            pipeline.addHandlers(context.getService().getOutHandlers());
            pipeline.addHandlers(context.getXFire().getOutHandlers());
            OutMessage msg = context.getExchange().getOutMessage();
            pipeline.addHandlers(msg.getChannel().getTransport().getOutHandlers());

            context.setOutPipeline(pipeline);
        }
    }
}
