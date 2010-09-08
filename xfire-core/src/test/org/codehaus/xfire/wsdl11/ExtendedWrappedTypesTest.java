package org.codehaus.xfire.wsdl11;

import java.util.Collection;
import java.util.Map;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ExtendedWrappedTypesTest
    extends AbstractXFireTest
{
    public void testVisitor()
        throws Exception
    {
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("extendedWrappedTypes.wsdl"));
        builder.setBindingProvider(new MessageBindingProvider());
        builder.build();
        
        Map serviceMap = builder.getServices();
        assertEquals(1, serviceMap.size());
        
        Collection services = builder.getAllServices();        
        assertEquals(1, services.size());
        
        Service service = (Service) services.iterator().next();
        
        Collection operations = service.getServiceInfo().getOperations();
        assertEquals(2, operations.size());
        
        OperationInfo opInfo = (OperationInfo) operations.iterator().next();
        assertEquals(1, opInfo.getInputMessage().size());
        assertEquals(1, opInfo.getOutputMessage().size());
    }
    
}
