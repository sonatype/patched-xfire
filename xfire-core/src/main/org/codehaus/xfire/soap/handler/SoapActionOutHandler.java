package org.codehaus.xfire.soap.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * Sets the SOAP action on an outgoing invocation.
 * 
 * @author Dan Diephouse
 */
public class SoapActionOutHandler
    extends AbstractHandler
{
    public SoapActionOutHandler()
    {
        super();
        setPhase(Phase.TRANSPORT);
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        Service service = context.getService();
        if (service == null) return;
        
        OperationInfo op = context.getExchange().getOperation();
        AbstractSoapBinding binding = (AbstractSoapBinding) context.getBinding();
        
        String action = binding.getSoapAction(op);
        if (action != null)
            context.getOutMessage().setProperty(SoapConstants.SOAP_ACTION, action);
    }
}
