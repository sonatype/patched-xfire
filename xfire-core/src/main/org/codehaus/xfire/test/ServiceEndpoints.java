package org.codehaus.xfire.test;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;

/**
 * Contains various <code>ServiceEndpoint</code> implementations. Mainly used throughout all the test classes which need
 * <code>ServiceEndpoint</code> to test their code.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see Service
 */
public class ServiceEndpoints
{
    private ServiceEndpoints()
    {
    }

    /**
     * Returns the endpoint for an echo service. The returned endpoint has the namespace
     * <code>http://test.xfire.codehaus.org</code>, and it's name is <code>Echo</code>. It has on operation named
     * <code>echo</code>, with an input message <code>echoRequest</code> and an output message
     * <code>echoResponse</code>. Both messages contain a single part, named <code>echoRequestin0</code> and
     * <code>echoResponseout</code> respectively.
     * <p/>
     * The endpoints <code>Class</code> and <code>Method</code> are mapped to {@link EchoImpl}.
     *
     * @return an echo service endpoint.
     */
    public static Service getEchoService()
    {
        Class echoClass = EchoImpl.class;
        Method echoMethod = null;
        try
        {
            echoMethod = echoClass.getMethod("echo", new Class[]{String.class});
        }
        catch (NoSuchMethodException e)
        {
            throw new XFireRuntimeException("Could not find echo method on Echo class", e);
        }
        ServiceInfo serviceInfo = new ServiceInfo(new QName("http://test.xfire.codehaus.org", "EchoPortType"),
                                                  echoClass);
        OperationInfo operation = serviceInfo.addOperation("echo", echoMethod);
        MessageInfo inputMessage = operation.createMessage(new QName("echoRequest"));
        operation.setInputMessage(inputMessage);
        MessageInfo outputMessage = operation.createMessage(new QName("echoResponse"));
        operation.setOutputMessage(outputMessage);
        inputMessage.addMessagePart(new QName("echoRequestin0"), String.class);
        outputMessage.addMessagePart(new QName("echoResponseout"), String.class);

        Service service = new Service(serviceInfo);
        service.setName(new QName("http://test.xfire.codehaus.org", "Echo"));
        return service;
    }

    /**
     * Returns the endpoint for an echo service with faults. This method returns the same as {@link #getEchoService()},
     * but adds a fault to the operation. The fault is named <code>echoFault</code>, and has one part
     * <code>echoFault0</code>.
     *
     * @return the echo service endpoint.
     */
    public static Service getEchoFaultService()
    {
        Service endpoint = getEchoService();
        OperationInfo operation = endpoint.getServiceInfo().getOperation("echo");
        FaultInfo fault = operation.addFault("echoFault");
        fault.addMessagePart(new QName("echoFault0"), String.class);

        return endpoint;
    }
}
