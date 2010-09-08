package org.codehaus.xfire.wsdl11;

import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartContainer;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class HeaderTest
    extends AbstractXFireTest
{
    public void testVisitor()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("echoHeader.wsdl"));
        builder.setBindingProvider(new MessageBindingProvider());
        builder.build();
        
        Map serviceMap = builder.getServices();
        assertEquals(1, serviceMap.size());
        
        Collection services = builder.getAllServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        
        QName name = service.getName();
        assertNotNull(name);
        assertEquals(new QName("urn:Echo", "Echo"), name);
        
        Collection operations = service.getServiceInfo().getOperations();
        assertEquals(1, operations.size());
        
        OperationInfo opInfo = (OperationInfo) operations.iterator().next();
        assertEquals("echo", opInfo.getName());
        
        // Check the input message
        MessageInfo message = opInfo.getInputMessage();
        assertNotNull(message);
        
        Collection parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        MessagePartInfo part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "echoRequest"), part.getName());
        
        // Check the output message
        message = opInfo.getOutputMessage();
        assertNotNull(message);
        
        parts = message.getMessageParts();
        assertEquals(1, parts.size());
        
        part = (MessagePartInfo) parts.iterator().next();
        assertEquals(new QName("urn:Echo", "echoResponse"), part.getName());
        
        // Is the SOAP binding stuff around?
        AbstractSoapBinding soapBinding = (AbstractSoapBinding) service.getBindings().iterator().next();
        assertNotNull(soapBinding);
        assertEquals("literal", soapBinding.getUse());
        assertEquals("", soapBinding.getSoapAction(opInfo));
        
        MessagePartContainer c = soapBinding.getHeaders(opInfo.getInputMessage());
        assertEquals(1, c.getMessageParts().size());
        part = c.getMessagePart(new QName("urn:Echo", "echoHeader"));
        assertNotNull(part);
        
        Collection endpoints = service.getEndpoints();
        assertEquals(1, endpoints.size());
        
        Endpoint endpoint = (Endpoint) endpoints.iterator().next();
        assertEquals(new QName("urn:Echo", "EchoHttpPort"), endpoint.getName());
        assertNotNull(endpoint.getBinding());
        assertEquals("http://localhost:8080/xfire/services/Echo", endpoint.getUrl());
    }
    
}
