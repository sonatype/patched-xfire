package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.stax.DepthXMLStreamReader;

public class RPCBinding
    extends WrappedBinding
{
    public RPCBinding()
    {
    }

    public void readMessage(InMessage inMessage, MessageContext context)
        throws XFireFault
    {
        Service endpoint = context.getService();
        
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getInMessage().getXMLStreamReader());
        
        if ( !STAXUtils.toNextElement(dr) )
            throw new XFireFault("There must be a method name element.", XFireFault.SENDER);
        
        String opName = dr.getLocalName();
        if (isClientModeOn(context))
            opName = opName.substring(0, opName.lastIndexOf("Response"));
        
        OperationInfo operation = endpoint.getServiceInfo().getOperation( opName );

        if (operation == null)
            throw new XFireFault("Could not find operation: " + opName, XFireFault.SENDER);
        
        // Move from operation element to whitespace or start element
        nextEvent(dr);
        
        setOperation(operation, context);

        if (operation == null)
        {
            throw new XFireFault("Invalid operation.", XFireFault.SENDER);
        }

        Service service = context.getService();
        
        MessageInfo msg;
        if (isClientModeOn(context))
            msg = operation.getOutputMessage();
        else
            msg = operation.getInputMessage();
        
        while(STAXUtils.toNextElement(dr))
        {
            MessagePartInfo p = (MessagePartInfo) msg.getMessageParts().get(parameters.size());

            if (p == null)
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }
            
            QName name;
            if (p.getSchemaType().isAbstract())
            {
                name = new QName(service.getTargetNamespace(), dr.getLocalName());
            }
            else
            {
                name = dr.getName();
            }
            
            if (!p.getName().equals(name))
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }
            
            parameters.add( endpoint.getBindingProvider().readParameter(p, dr, context) );
        }
        
        context.getInMessage().setBody(parameters);
    }

    protected String getBoundNamespace(MessageContext context, MessagePartInfo p)
    {
        if (p.isSchemaElement() ||
                ((AbstractSoapBinding) context.getBinding()).getUse().equals(SoapConstants.USE_ENCODED))
            return p.getName().getNamespaceURI();
        else
            return "";
    }

    public Object clone()
    {
        return new RPCBinding();
    }    
}
