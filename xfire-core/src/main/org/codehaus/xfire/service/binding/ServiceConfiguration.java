package org.codehaus.xfire.service.binding;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;

public abstract class ServiceConfiguration
{
    public abstract void setServiceFactory(ObjectServiceFactory serviceFactory);
    
    public Boolean isOperation(final Method method)
    {
        return null;
    }

    public Boolean isOutParam(Method method, int j)
    {
        return null;
    }
    
    public  Boolean isInParam(Method method, int j)
    {
        return null;
    }

    public  QName getInputMessageName(final OperationInfo op)
    {
        return null;
    }

    public  QName getOutputMessageName(final OperationInfo op)
    {
        return null;
    }

    public  Boolean hasOutMessage(String mep)
    {
        return null;
    }

    public  QName getFaultName(Service service, OperationInfo o, Class exClass, Class beanClass)
    {
        return null;
    }

    public  String getAction(OperationInfo op)
    {
        return null;
    }

    public  Boolean isHeader(Method method, int j)
    {
        return null;
    }

    /**
     * Creates a name for the operation from the method name. If an operation
     * with that name already exists, a name is create by appending an integer
     * to the end. I.e. if there is already two methods named
     * <code>doSomething</code>, the first one will have an operation name of
     * "doSomething" and the second "doSomething1".
     * 
     * @param service
     * @param method
     */
    public  String getOperationName(ServiceInfo service, Method method)
    {
        return null;
    }

    public  String getMEP(final Method method)
    {
        return null;
    }

    public  Boolean isAsync(final Method method)
    {
        return null;
    }

    public  QName getInParameterName(final Service service,
                             final OperationInfo op,
                             final Method method,
                             final int paramNumber,
                             final boolean doc)
    {
        return null;
    }

    public  QName getOutParameterName(final Service service,
                              final OperationInfo op,
                              final Method method,
                              final int paramNumber,
                              final boolean doc)
    {
        return null;
    }

    public QName getPortType(Class clazz)
    {
        return null;
    }
}
