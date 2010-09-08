package org.codehaus.xfire.fault;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

public class NoServiceFaultTest
    extends AbstractXFireTest
{

    /**
     * Tests to see what happens when there is no service and we invoke.
     * @throws Exception
     */
    public void testInvoke()
        throws Exception
    {
        Document response = invokeService((String)null, "/org/codehaus/xfire/echo11.xml");

        assertNull(response);
    }
}
