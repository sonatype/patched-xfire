package org.codehaus.xfire.soap.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.soap.SoapSerializer;

public class FaultSoapSerializerHandler
    extends AbstractHandler
{

    public FaultSoapSerializerHandler() 
    {
        super();
        setPhase(Phase.POST_INVOKE);
    }

    /**
     * Validates that the mustUnderstand and role headers are processed correctly.
     *
     * @param context
     * @throws XFireFault
     */
    public void invoke(MessageContext context)
        throws Exception
    {
        OutMessage msg = (OutMessage) context.getExchange().getFaultMessage();
        MessageSerializer serializer = msg.getSerializer();

        msg.setSerializer(new SoapSerializer(serializer));
    }
}
