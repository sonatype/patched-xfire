package org.codehaus.xfire;

import java.util.Collections;
import java.util.List;

import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.test.AbstractXFireTest;

public class XFirePhaseTest extends AbstractXFireTest
{
    public void testPhases() throws Exception
    {
        List inPhases = getXFire().getInPhases();
        assertNotNull(inPhases);
        
        Collections.sort(inPhases);
        assertTrue(new Phase(Phase.TRANSPORT, 1000).equals(inPhases.get(0)));
        
        List outPhases = getXFire().getOutPhases();
        assertNotNull(outPhases);
    }
}