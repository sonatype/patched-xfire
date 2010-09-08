package org.codehaus.xfire.addressing;

import java.util.Iterator;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;

/**
 * Addressing information for an operation. 
 * 
 * Warning: This class may change in the future!
 * 
 * @author Dan Diephouse
 */
public class AddressingOperationInfo
{
    private String inAction;
    private String outAction;
    private String to;
    private EndpointReference replyTo;
    private EndpointReference faultTo;
    private EndpointReference from;
    
    private OperationInfo operationInfo;
    
    public final static String ADDRESSING_OPERATION_KEY = "addressingOperationInfo";
    
    public AddressingOperationInfo() {}
    
    public AddressingOperationInfo(String inAction, OperationInfo op)
    {
        this(inAction, inAction + "Ack", op);
    }
    
    public AddressingOperationInfo(String inAction, String outAction, String to, OperationInfo op)
    {
        this.inAction = inAction;
        this.outAction = outAction;
        this.to=to;
        op.setProperty(ADDRESSING_OPERATION_KEY, this);
        this.operationInfo = op;
    }
    
    public AddressingOperationInfo(String inAction, String outAction, OperationInfo op)
    {
        this(inAction, outAction, null, op);
    }

    public AddressingOperationInfo(OperationInfo op)
    {
        this(op.getService().getService().getTargetNamespace() + "#" + op.getName(), op);
    }

    public static AddressingOperationInfo getAddressingOperationInfo(OperationInfo op)
    {
        return (AddressingOperationInfo) op.getProperty(ADDRESSING_OPERATION_KEY);
    }
    
    public static String getInAction(OperationInfo op)
    {
        AddressingOperationInfo aoi = getAddressingOperationInfo(op);
        if (aoi == null) return null;
        
        return aoi.getInAction();
    }
    
    public static String getOutAction(OperationInfo op)
    {
        AddressingOperationInfo aoi = getAddressingOperationInfo(op);
        if (aoi == null) return null;
        
        return aoi.getOutAction();
    }
    
    public static AddressingOperationInfo getOperationByInAction(ServiceInfo service, String name)
    {
        for (Iterator itr = service.getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo op = (OperationInfo) itr.next();
            AddressingOperationInfo aoi = getAddressingOperationInfo(op);
            
            if (aoi == null) continue;
            
            if (aoi.getInAction() != null && aoi.getInAction().equals(name))
            {
                return aoi;
            }
        }
        
        if (!name.equals("*"))
        {
            return getOperationByInAction(service, "*");
        }
        
        return null;
    }
    
    public static AddressingOperationInfo getOperationByOutAction(ServiceInfo service, String name)
    {
        for (Iterator itr = service.getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo op = (OperationInfo) itr.next();
            AddressingOperationInfo aoi = getAddressingOperationInfo(op);
            
            if (aoi == null) continue;
            
            if (aoi.getOutAction() != null && aoi.getOutAction().equals(name))
            {
                return aoi;
            }
        }
        
        if (!name.equals("*"))
        {
            return getOperationByOutAction(service, "*");
        }
        
        return null;
    }
    
    public String getInAction()
    {
        return inAction;
    }
    
    public void setInAction(String inAction)
    {
        this.inAction = inAction;
    }
    
    public String getOutAction()
    {
        return outAction;
    }
    
    public void setOutAction(String outAction)
    {
        this.outAction = outAction;
    }
    public String getTo()
    {
        return to;
    }
    public void setTo(String to)
    {
        this.to = to;
    }

    public OperationInfo getOperationInfo()
    {
        return operationInfo;
    }

    public void setOperationInfo(OperationInfo operationInfo)
    {
        this.operationInfo = operationInfo;
    }

    public EndpointReference getFaultTo()
    {
        return faultTo;
    }

    public void setFaultTo(EndpointReference faultTo)
    {
        this.faultTo = faultTo;
    }

    public EndpointReference getFrom()
    {
        return from;
    }

    public void setFrom(EndpointReference from)
    {
        this.from = from;
    }

    public EndpointReference getReplyTo()
    {
        return replyTo;
    }

    public void setReplyTo(EndpointReference replyTo)
    {
        this.replyTo = replyTo;
    }
}
