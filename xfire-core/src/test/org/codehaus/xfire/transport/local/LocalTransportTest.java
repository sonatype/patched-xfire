package org.codehaus.xfire.transport.local;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Channel;

public class LocalTransportTest
    extends AbstractXFireTest
{
    public void testLocalTransport() throws Exception
    {
        LocalTransport transport = new LocalTransport();
        
        Channel c1 = transport.createChannel("uri1");
        Channel c2 = transport.createChannel("uri1");
        
        assertEquals(c1, c2);
        
        transport.dispose();
    }
}
