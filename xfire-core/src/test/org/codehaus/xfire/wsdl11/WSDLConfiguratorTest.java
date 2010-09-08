package org.codehaus.xfire.wsdl11;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.test.Echo;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WSDLConfiguratorTest
    extends AbstractXFireTest
{
    public void testVisitor()
        throws Exception
    {
        ServiceFactory sf = new ObjectServiceFactory(getTransportManager(), 
                                                     new MessageBindingProvider()) 
        {
            protected String getTargetNamespace(Class clazz) 
            {
                return "urn:Echo";
            }
        };
        
        Map properties = new HashMap();
        properties.put(ObjectServiceFactory.PORT_TYPE, new QName("urn:Echo", "EchoPortType"));
        
        Service service = sf.create(Echo.class, 
                                    new QName("urn:Echo", "Echo"),
                                    getClass().getResource("echo.wsdl"), 
                                    null);
        
        assertEquals(1, service.getBindings().size());
        assertEquals(1, service.getEndpoints().size());
    }
}
