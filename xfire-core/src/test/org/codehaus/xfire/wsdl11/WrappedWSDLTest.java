package org.codehaus.xfire.wsdl11;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.http.SoapHttpTransport;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WrappedWSDLTest
    extends AbstractXFireTest
{
    public void testVisitor()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("echoWrapped.wsdl"));
        builder.setBindingProvider(new MessageBindingProvider());
        builder.build();
        
        Collection services = builder.getAllServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        Collection operations = service.getServiceInfo().getOperations();
        assertEquals(1, operations.size());
        
        Iterator itr = operations.iterator();
        OperationInfo opInfo = (OperationInfo) itr.next();
        assertEquals("echo", opInfo.getName());
        assertEquals("urn:Echo:schema", opInfo.getInputMessage().getName().getNamespaceURI());
        assertEquals("urn:Echo:schema", opInfo.getOutputMessage().getName().getNamespaceURI());
        
        // Check the input message
        MessageInfo message = opInfo.getInputMessage();
        Collection parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        MessagePartInfo part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo:schema", "text"), part.getName());
        
        // and now the output...
        message = opInfo.getOutputMessage();
        parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        assertTrue(service.getServiceInfo().isWrapped());
        
        part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo:schema", "text"), part.getName());

        Collection endpoints = service.getEndpoints();
        assertEquals(1, endpoints.size());
        
        Endpoint endpoint = (Endpoint) endpoints.iterator().next();
        assertEquals(new QName("urn:Echo", "EchoHttpPort"), endpoint.getName());
        assertEquals(SoapHttpTransport.SOAP11_HTTP_BINDING, endpoint.getBinding().getBindingId());
        assertEquals("http://localhost:8080/xfire/services/Echo", endpoint.getUrl());
    }
    
    public void testBadWrapped()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("echoBadWrapped.wsdl"));
        builder.setBindingProvider(new MessageBindingProvider());
        builder.build();
        
        Collection services = builder.getAllServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        Collection operations = service.getServiceInfo().getOperations();
        assertEquals(1, operations.size());
        
        Iterator itr = operations.iterator();
         /*
          * The echo2 operation shouldn't be wrapped since it has attributes
          */
        OperationInfo opInfo = (OperationInfo) itr.next();
        assertEquals("echo2", opInfo.getName());
        
        // Check the input message
        MessageInfo message = opInfo.getInputMessage();
        List parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        MessagePartInfo part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "echo2"), part.getName());
        
        // Check the output message
        message = opInfo.getOutputMessage();
        assertNotNull(message);
        
        parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "echo2Response"), part.getName());
        
        // Is the SOAP binding stuff around?
        AbstractSoapBinding binding = (AbstractSoapBinding) service.getBinding(new QName("urn:Echo", "EchoHttpBinding"));
        assertNotNull(binding);
        assertEquals("literal", binding.getUse());
        //assertEquals("urn:Echo/echo2", binding.getSoapAction(opInfo));
        
    }
    
    public void testClient()
        throws Exception
    {
        //Client client = new Client(new URL("echo.wsdl"));
    }
}
