package org.codehaus.xfire.soap;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.service.binding.DocumentBinding;
import org.codehaus.xfire.service.binding.MessageBinding;
import org.codehaus.xfire.service.binding.RPCBinding;
import org.codehaus.xfire.service.binding.WrappedBinding;

/**
 * A SOAP Binding which contains information on how SOAP is mapped to the service model.
 * @author Dan Diephouse
 */
public abstract class AbstractSoapBinding extends Binding
{
    private String style = SoapConstants.STYLE_DOCUMENT;
    private String use = SoapConstants.USE_LITERAL;
    
    private Map op2action = new HashMap();
    private Map action2Op = new HashMap();

    public AbstractSoapBinding(QName name, String bindingId, Service serviceInfo)
    {
        super(name, bindingId, serviceInfo);
    }
    
    public abstract SoapVersion getSoapVersion();
    
    public String getStyle()
    {
        return style;
    }

    public String getStyle(OperationInfo operation)
    {
        return style;
    }
    
    public OperationInfo getOperationByAction(String action)
    {
        OperationInfo op = (OperationInfo) action2Op.get(action);
        
        if (op == null)
        {
            op = (OperationInfo) action2Op.get("*");
        }
        
        return op;
    }
    
    /**
     * Get the soap action for an operation. Will never return null.
     * @param operation
     * @return
     */
    public String getSoapAction(OperationInfo operation)
    {
        String action = (String) op2action.get(operation);
        
        if (action == null) action = "";
        
        return action;
    }
    
    public void setSoapAction(OperationInfo operation, String action)
    {
        op2action.put(operation, action);
        action2Op.put(action, operation);
    }
    
    public String getUse()
    {
        return use;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public void setUse(String use)
    {
        this.use = use;
    }

    public static AbstractBinding getSerializer(String style, String use)
    {
        if (style.equals(SoapConstants.STYLE_WRAPPED) && use.equals(SoapConstants.USE_LITERAL))
        {
            return new WrappedBinding();
        }
        else if (style.equals(SoapConstants.STYLE_DOCUMENT)
                && use.equals(SoapConstants.USE_LITERAL))
        {
            return new DocumentBinding();
        }
        else if (style.equals(SoapConstants.STYLE_RPC) && use.equals(SoapConstants.USE_LITERAL))
        {
            return new RPCBinding();
        }
        else if (style.equals(SoapConstants.STYLE_RPC) && use.equals(SoapConstants.USE_ENCODED))
        {
            return new RPCBinding();
        }
        else if (style.equals(SoapConstants.STYLE_MESSAGE) && use.equals(SoapConstants.USE_LITERAL))
        {
            return new MessageBinding();
        }
        else
        {
            throw new UnsupportedOperationException("Service style/use not supported: " + style
                    + "/" + use);
        }
    }
}
