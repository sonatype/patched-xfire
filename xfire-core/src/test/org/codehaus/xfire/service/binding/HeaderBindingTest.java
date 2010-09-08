package org.codehaus.xfire.service.binding;


import java.lang.reflect.Method;

import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;
import org.jdom.Element;

public class HeaderBindingTest
        extends AbstractXFireTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();
    }

    public void testHeaders()
            throws Exception
    {
        ObjectServiceFactory osf = new ObjectServiceFactory(getXFire().getTransportManager(), 
                                                            new MessageBindingProvider())
        {
            protected boolean isHeader(Method method, int j)
            {
                return (j == 1);
            }
        };
        osf.setStyle(SoapConstants.STYLE_MESSAGE);
        
        Service service = osf.create(HeaderService.class, "HeaderService", "urn:HeaderService", null);
        getXFire().getServiceRegistry().register(service);
        
        OperationInfo op = (OperationInfo) service.getServiceInfo().getOperations().iterator().next();
        assertEquals(2, op.getInputMessage().getMessageParts().size());
        
        Binding binding = (Binding) service.getBindings().iterator().next();
        assertEquals(1, binding.getHeaders(op.getInputMessage()).getMessageParts().size());
        
        Document response = invokeService("HeaderService", "header.xml");
        assertNotNull(HeaderService.a);
        assertEquals("a", HeaderService.a.getName());
        assertNotNull(HeaderService.b);
        assertEquals("b", HeaderService.b.getName());
        assertNotNull(HeaderService.header);
        assertEquals("header", HeaderService.header.getName());
    }
    
    public static class HeaderService
    {
        static Element a;
        static Element b;
        static Element header;
        
        public void doSomething(Element a, Element header, Element b) 
        {
            HeaderService.a = a;
            HeaderService.b = b;
            HeaderService.header = header;
        }
    }
}