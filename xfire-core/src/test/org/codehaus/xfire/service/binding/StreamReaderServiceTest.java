package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;

public class StreamReaderServiceTest extends AbstractXFireTest
{
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        Service service = getServiceFactory().create(StreamReaderService.class);
        getServiceRegistry().register(service);
    }

    public void testStreamReaderService() throws Exception
    {
        invokeService("StreamReaderService", "/org/codehaus/xfire/echo11.xml");
    }
}
