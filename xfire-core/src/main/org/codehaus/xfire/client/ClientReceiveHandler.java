package org.codehaus.xfire.client;

import java.util.List;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.binding.ServiceInvocationHandler;

class ClientReceiveHandler extends AbstractHandler
{
    private Invocation call;
    
    public ClientReceiveHandler(Invocation call)
    {
        super();
        
        setPhase(Phase.SERVICE);
        this.call = call;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        if (context.getCurrentMessage().equals(context.getExchange().getInMessage()))
        {
            List body = (List) context.getCurrentMessage().getBody();
            Binding binding = context.getBinding();
            MessageInfo msgInfo = context.getExchange().getOperation().getOutputMessage();
            Object result = ServiceInvocationHandler.readHeaders(context,
                                                                 binding.getHeaders(msgInfo),
                                                                 (Object[]) context.getOutMessage().getBody());
            
            if (result != null)
            {
                body.add(result);
            }
            
            call.receive(body);
        }
        else if (context.getCurrentMessage().equals(context.getExchange().getFaultMessage()))
            call.receiveFault((Exception) context.getCurrentMessage().getBody());                
    }
}
