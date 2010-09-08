package org.codehaus.xfire.service.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.xml.namespace.QName;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.FaultInfoException;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.ParamReader;
import org.codehaus.xfire.util.ServiceUtils;

public class DefaultServiceConfiguration extends ServiceConfiguration
{
    private ObjectServiceFactory serviceFactory;
    
    public DefaultServiceConfiguration()
    {
        super();
    }

    public ObjectServiceFactory getServiceFactory()
    {
        return serviceFactory;
    }

    public void setServiceFactory(ObjectServiceFactory serviceFactory)
    {
        this.serviceFactory = serviceFactory;
    }

    public Boolean isOperation(final Method method)
    {
        if(serviceFactory.getIgnoredClasses().contains(method.getDeclaringClass().getName())) 
            return Boolean.FALSE;

        final int modifiers = method.getModifiers();

        return Boolean.valueOf(Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers));
    }

    public Boolean isOutParam(Method method, int j)
    {
        //return new Boolean(j == -1);
        return Boolean.valueOf(j == -1);
    }

    public Boolean isInParam(Method method, int j)
    {
       // return new Boolean((j >= 0));
    	 return Boolean.valueOf(j >= 0);
    }
    
    public QName getInputMessageName(final OperationInfo op)
    {
        return new QName(op.getService().getPortType().getNamespaceURI(), op.getName() + "Request");
    }

    public QName getOutputMessageName(final OperationInfo op)
    {
        return new QName(op.getService().getPortType().getNamespaceURI(), op.getName() + "Response");
    }
    
    public Boolean hasOutMessage(String mep)
    {
        if (mep.equals(SoapConstants.MEP_IN)) 
            return Boolean.FALSE;
        
        return Boolean.TRUE;
    }
    
    public QName getFaultName(Service service, OperationInfo o, Class exClass, Class beanClass)
    {
        if (FaultInfoException.class.isAssignableFrom(exClass))
        {
            Method method;
            try
            {
                method = exClass.getMethod("getFaultName", new Class[0]);
                return (QName) method.invoke(null, new Object[0]);
                
            }
            catch (NoSuchMethodException e)
            {
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Couldn't access getFaultName method.", e);
            }
        }
        
        String name = ServiceUtils.makeServiceNameFromClassName(beanClass);
        return new QName(service.getTargetNamespace(), name);
    }
    
    public String getAction(OperationInfo op)
    {
        return "";
    }

    public Boolean isHeader(Method method, int j)
    {
        return Boolean.FALSE;
    }

    /**
     * Creates a name for the operation from the method name. If an operation with that name
     * already exists, a name is create by appending an integer to the end. I.e. if there is already
     * two methods named <code>doSomething</code>, the first one will have an operation name of
     * "doSomething" and the second "doSomething1".
     * 
     * @param service
     * @param method
     */
    public String getOperationName(ServiceInfo service, Method method)
    {
        if (service.getOperation(method.getName()) == null)
        {
            return method.getName();
        }

        int i = 1;
        while (true)
        {
            String name = method.getName() + i;
            if (service.getOperation(name) == null)
            {
                return name;
            }
            else
            {
                i++;
            }
        }
    }

    public String getMEP(final Method method)
    {
        if (serviceFactory.isVoidOneWay() && method.getReturnType().equals(void.class))
        {
            return SoapConstants.MEP_IN;
        }
        
        return SoapConstants.MEP_ROBUST_IN_OUT;
    }

    public Boolean isAsync(final Method method)
    {
        return Boolean.FALSE;
    }

    public QName getInParameterName(final Service endpoint,
                                       final OperationInfo op,
                                       final Method method,
                                       final int paramNumber,
                                       final boolean doc)
    {
        QName suggestion = serviceFactory.getBindingProvider().getSuggestedName(endpoint, op, paramNumber);
        
        if (suggestion != null) return suggestion;

        return new QName(endpoint.getServiceInfo().getPortType().getNamespaceURI(), 
                         createName(method, paramNumber, op.getInputMessage().size(), doc, "in"));
    }

    public QName getOutParameterName(final Service endpoint, 
                                     final OperationInfo op, 
                                     final Method method,
                                     final int paramNumber,
                                     final boolean doc)
    {
        QName suggestion = serviceFactory.getBindingProvider().getSuggestedName(endpoint, op, paramNumber);
        
        if (suggestion != null) return suggestion;
        
        String pName = (doc) ? method.getName() : "";
        
        return new QName(endpoint.getServiceInfo().getPortType().getNamespaceURI(), 
                         createName(method, paramNumber, op.getOutputMessage().size(), doc, "out"));
    }
    
    private String createName(final Method method,
                              final int paramNumber,
                              final int currentSize,
                              boolean addMethodName,
                              final String flow)
    {
        String paramName = "";
        
        if (paramNumber != -1)
        {
            String[] names = ParamReader.getParameterNamesFromDebugInfo(method); 
            
            //get the spcific parameter name from the parameter Number
            if (names != null && names[paramNumber] != null)
            {
                paramName = names[paramNumber];
                addMethodName = false;
            }
            else
            {
                paramName = flow + currentSize;        
            }
        }
        else
        {
            paramName = flow;        
        }
        
        paramName = (addMethodName) ? method.getName() + paramName : paramName;
        
        return paramName;
    }
}
