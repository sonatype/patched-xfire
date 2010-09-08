package org.codehaus.xfire.service.binding;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.SoapFaultSerializer;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.jdom.Element;

public class ObjectServiceFactoryTest
        extends AbstractXFireTest
{
    private ObjectServiceFactory objectServiceFactory;

    public void setUp()
            throws Exception
    {
        super.setUp();

        objectServiceFactory = new ObjectServiceFactory(getXFire().getTransportManager(),
                                                        new MessageBindingProvider());
    }

    public void testMakeServiceNameFromClassName()
            throws Exception
    {
        String serviceName = objectServiceFactory.makeServiceNameFromClassName(ObjectServiceFactoryTest.class);
        assertNotNull(serviceName);
        assertEquals("Invalid service name", "ObjectServiceFactoryTest", serviceName);
    }

    public void testCreateClass()
            throws Exception
    {
        Service endpoint = objectServiceFactory.create(Echo.class);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getServiceInfo();
        assertEquals(new QName("http://binding.service.xfire.codehaus.org", "Echo"), endpoint.getName());
        assertTrue(endpoint.getFaultSerializer() instanceof SoapFaultSerializer);
        
        assertTrue(service.isWrapped());
        
        AbstractSoapBinding soapOp = (AbstractSoapBinding) endpoint.getBindings().iterator().next();
        assertEquals(SoapConstants.USE_LITERAL, soapOp.getUse());
    }

    public void testCreateNameNamespaceVersionStyleUseEncodingStyle()
            throws Exception
    {
        Map properties = new HashMap();
        properties.put(ObjectServiceFactory.STYLE, SoapConstants.STYLE_RPC);
        properties.put(ObjectServiceFactory.USE, SoapConstants.USE_ENCODED);
        
        Service endpoint = objectServiceFactory.create(Echo.class,
                                                       "EchoService",
                                                       "http://xfire.codehaus.org",
                                                       properties);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getServiceInfo();
        assertEquals(new QName("http://xfire.codehaus.org", "EchoService"), endpoint.getName());
        
        AbstractSoapBinding binding = (AbstractSoapBinding) endpoint.getBindings().iterator().next();
        assertEquals(SoapConstants.STYLE_RPC, binding.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, binding.getUse());

        assertEquals(3, endpoint.getBindings().size());
    }

    public void testCreateNameNamespaceNull()
            throws Exception
    {
        Map properties = new HashMap();
        properties.put(ObjectServiceFactory.STYLE, SoapConstants.STYLE_RPC);
        properties.put(ObjectServiceFactory.USE, SoapConstants.USE_ENCODED);
        
        Service endpoint = objectServiceFactory.create(Echo.class,
                                                       (String) null,
                                                       null,
                                                       properties);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getServiceInfo();
        assertEquals(new QName("http://binding.service.xfire.codehaus.org", "Echo"), endpoint.getName());

        AbstractSoapBinding binding = (AbstractSoapBinding) endpoint.getBindings().iterator().next();
        assertEquals(SoapConstants.STYLE_RPC, binding.getStyle());
        assertEquals(SoapConstants.USE_ENCODED, binding.getUse());
    }
    
    public void testOverriddenBindings()
        throws Exception
    {
        Map properties = new HashMap();
        properties.put(ObjectServiceFactory.CREATE_DEFAULT_BINDINGS, Boolean.FALSE);
       
        ArrayList s11 = new ArrayList();
        s11.add(LocalTransport.BINDING_ID);
        properties.put(ObjectServiceFactory.SOAP11_TRANSPORTS, s11);
        
        ArrayList s12 = new ArrayList();
        s12.add(LocalTransport.BINDING_ID);
        properties.put(ObjectServiceFactory.SOAP12_TRANSPORTS, s12);
        
        Service endpoint = objectServiceFactory.create(Echo.class, (String) null, null, properties);
        assertNotNull(endpoint);
        ServiceInfo service = endpoint.getServiceInfo();
        assertEquals(new QName("http://binding.service.xfire.codehaus.org", "Echo"), endpoint
                .getName());

        assertEquals(2, endpoint.getBindings().size());
    }

    public void testOverridenNames()
        throws Exception
    {
        Service service = getServiceFactory().create(OperationNameService.class);
        
        assertTrue( service.getServiceInfo().getOperation("doSomething") != null );
        assertTrue( service.getServiceInfo().getOperation("doSomething1") != null );
    }

    public class OperationNameService
    {
        public void doSomething() {}
        public void doSomething(String bleh) {}
    }

    public void testHeaders()
            throws Exception
    {
        ObjectServiceFactory osf = new ObjectServiceFactory(getXFire().getTransportManager(), 
                                                            new MessageBindingProvider())
        {

            protected boolean isHeader(Method method, int j)
            {
                if (j == -1) return false;
                
                return method.getParameterTypes()[j].equals(String.class);
            }
        };
        
        Service service = osf.create(HeaderService.class);
        ServiceInfo info = service.getServiceInfo();
        
        MessageInfo inMsg =info.getOperation("doSomething").getInputMessage();
        assertEquals(1, inMsg.getMessageParts().size());
        
        AbstractSoapBinding soapOp = (AbstractSoapBinding) service.getBindings().iterator().next();
        MessagePartInfo part = soapOp.getHeaders(inMsg).getMessagePart(new QName(service.getName().getNamespaceURI(), "header"));
        assertNotNull(part);
    }
    
    public static class HeaderService
    {
        public void doSomething(Element a, String header) {};
    }
    
    public void testFaultInfo()
        throws Exception
    {
        Service service = objectServiceFactory.create(EchoWithFault.class);
        
        OperationInfo op = service.getServiceInfo().getOperation("echo");
        
        assertEquals(1, op.getFaults().size());
        
        FaultInfo info = op.getFault("EchoFault");
        assertNotNull(info);
        
        assertEquals(1, info.getMessageParts().size());
        MessagePartInfo mp = info.getMessagePart(new QName(service.getName().getNamespaceURI(),
                                                           "EchoFault"));
        assertEquals(EchoFault.class, mp.getTypeClass());
    }
}