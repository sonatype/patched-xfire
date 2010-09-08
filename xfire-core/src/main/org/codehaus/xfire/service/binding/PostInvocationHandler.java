package org.codehaus.xfire.service.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.DefaultFaultHandler;
import org.codehaus.xfire.handler.Phase;

public class PostInvocationHandler extends AbstractHandler {

    private static final Log logger = LogFactory.getLog(PostInvocationHandler.class.getName());
    public static final String RESPONSE_VALUE = "postInvocationHandler.responseValue";

    public PostInvocationHandler() {
        super();
        setPhase(Phase.SERVICE);
        after(ServiceInvocationHandler.class.getName());
    }

    public void invoke(MessageContext context) throws Exception {
        Object value = context.getProperty(RESPONSE_VALUE);
        
        if (context.getExchange().hasOutMessage())
        {
            OutMessage outMsg = context.getExchange().getOutMessage();
            ServiceInvocationHandler.writeHeaders(context, value);
            context.setCurrentMessage(outMsg);
            outMsg.setBody(new Object[] {value});
            outMsg.setSerializer(context.getBinding().getSerializer(context.getExchange().getOperation()));
            
            try
            {
                context.getOutPipeline().invoke(context);
            }
            catch (Exception e)
            { 
                logger.error(e);

                XFireFault fault = XFireFault.createFault(e);
                context.setProperty(DefaultFaultHandler.EXCEPTION, fault);
                
                context.getCurrentPipeline().pause();
                
                context.getCurrentPipeline().handleFault(fault, context);
                context.getInPipeline().handleFault(fault, context);

                context.getFaultHandler().invoke(context);
            }
        }
    }
}
