package org.codehaus.xfire.transport;

import java.util.Collection;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.http.HttpTransport;
import org.codehaus.xfire.transport.http.SoapHttpTransport;
import org.codehaus.xfire.transport.local.LocalTransport;

public class TransportManagerTest
    extends AbstractXFireTest
{
    public void testTM() throws Exception
    {
        DefaultTransportManager tm = new DefaultTransportManager();
        tm.initialize();
        assertEquals(4, tm.getTransports().size());
        
        HttpTransport transport = new HttpTransport();
        tm.register(transport);
        assertEquals(5, tm.getTransports().size());
        
        tm.unregister(transport);
        assertEquals(4, tm.getTransports().size()); 
    }
    
    public void testHTTPLookup()
    {
        TransportManager tm = getXFire().getTransportManager();

        assertEquals(4, tm.getTransports().size());
        
        Transport t = tm.getTransportForUri("http://localhost");
        assertTrue(t instanceof SoapHttpTransport);
        
        t = tm.getTransportForUri("https://localhost");
        assertTrue(t instanceof SoapHttpTransport);
        
        t = tm.getTransportForUri("xfire.local://Foo");
        assertTrue(t instanceof LocalTransport);
        
        Collection transports = tm.getTransportsForUri("http://localhost");
        assertEquals(2, transports.size());
    }
}
