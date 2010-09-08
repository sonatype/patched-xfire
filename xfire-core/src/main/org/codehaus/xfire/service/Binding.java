package org.codehaus.xfire.service;

import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

public abstract class Binding
    extends Extensible
{
    private QName name;

    private String bindingId;
    private boolean undefinedEndpointAllowed = true;
    private Service service;
    private Map op2serializer = new HashMap();
    private Map msg2parts = new HashMap();
    
    private MessageSerializer serializer;

    protected Binding(QName name, String bindingId, Service service)
    {
        this.name = name;
        this.bindingId = bindingId;
        this.service = service;
    }

    public QName getName()
    {
        return name;
    }

    public Service getService()
    {
        return service;
    }

    public String getBindingId()
    {
        return bindingId;
    }

    public boolean isUndefinedEndpointAllowed()
    {
        return undefinedEndpointAllowed;
    }

    public void setUndefinedEndpointAllowed(boolean undefinedEndpointAllowed)
    {
        this.undefinedEndpointAllowed = undefinedEndpointAllowed;
    }

    public abstract javax.wsdl.Binding createBinding(WSDLBuilder builder, PortType portType);

    public abstract Port createPort(WSDLBuilder builder, javax.wsdl.Binding wbinding);

    public abstract Port createPort(Endpoint endpoint,
                                    WSDLBuilder builder,
                                    javax.wsdl.Binding wbinding);

    
    public MessagePartContainer getHeaders(MessageInfo msg)
    {
        MessagePartContainer c = (MessagePartContainer) msg2parts.get(msg);
        if (c == null)
        {
            c = new HeaderPartContainer(msg.getOperation());
            msg2parts.put(msg, c);
        }
        
        return c;
    }

    public MessageSerializer getSerializer(OperationInfo operation)
    {
        MessageSerializer ser = (MessageSerializer) op2serializer.get(operation);
        if (ser == null)
        {
            ser = getSerializer();
        }
        
        return ser;
    }

    public void setSerializer(OperationInfo op, MessageSerializer ser)
    {
        op2serializer.put(op, ser);
    }
    
    public MessageSerializer getSerializer()
    {
        return serializer;
    }

    public void setSerializer(MessageSerializer serializer)
    {
        this.serializer = serializer;
    }
    
    static class HeaderPartContainer extends MessagePartContainer
    {
        public HeaderPartContainer(OperationInfo operation)
        {
            super(operation);
        }        
    }
}
