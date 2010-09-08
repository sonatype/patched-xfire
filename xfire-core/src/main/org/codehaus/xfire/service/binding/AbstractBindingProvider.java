package org.codehaus.xfire.service.binding;

import java.util.Iterator;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessagePartContainer;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;

public abstract class AbstractBindingProvider 
    implements BindingProvider
{
    protected static final int IN_PARAM = 0;
    protected static final int OUT_PARAM = 1;
    protected static final int FAULT_PARAM = 2;
    
    /**
     * Creates a type mapping for this class and registers it with the TypeMappingRegistry. This needs to be called
     * before initializeOperations().
     */
    public void initialize(Service endpoint)
    {
        for (Iterator itr = endpoint.getServiceInfo().getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo opInfo = (OperationInfo) itr.next();
            try
            {
                initializeMessage(endpoint, opInfo.getInputMessage(), IN_PARAM);
            }
            catch(XFireRuntimeException e)
            {
                e.prepend("Error initializing parameters for method " + opInfo.getMethod());
                throw e;
            }
            
            try
            {
                if (opInfo.hasOutput())
                    initializeMessage(endpoint, opInfo.getOutputMessage(), OUT_PARAM);
            }
            catch(XFireRuntimeException e)
            {
                e.prepend("Error initializing return value for method " + opInfo.getMethod());
                throw e;
            }
            
            try
            {
                for (Iterator faultItr = opInfo.getFaults().iterator(); faultItr.hasNext();)
                {
                    FaultInfo info = (FaultInfo) faultItr.next();
                    initializeMessage(endpoint, info, FAULT_PARAM);
                }
            }
            catch(XFireRuntimeException e)
            {
                e.prepend("Error initializing fault for method " + opInfo.getMethod());
                throw e;
            }
        }
    }


    public void initialize(Service endpoint, Binding binding)
    {
        for (Iterator itr = endpoint.getServiceInfo().getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo opInfo = (OperationInfo) itr.next();
            
            initializeMessage(endpoint, binding.getHeaders(opInfo.getInputMessage()), IN_PARAM);
            
            if (opInfo.hasOutput())
            {
                initializeMessage(endpoint, binding.getHeaders(opInfo.getOutputMessage()), OUT_PARAM);
            }
        }
    }
    
    protected void initializeMessage(Service service, MessagePartContainer container, int type) 
    {
    }
}
