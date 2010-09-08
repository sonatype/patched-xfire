package org.codehaus.xfire.handler;

import junit.framework.TestCase;

public class PhaseTest
        extends TestCase
{
     public void testEquals()
            throws Exception
    {
        Phase p1 = new Phase(Phase.TRANSPORT, 100);
        Phase p2 = new Phase(Phase.TRANSPORT, 100);
        
        assertEquals(p1, p2);
        
        Phase p3 = new Phase(Phase.TRANSPORT, 400);
        Phase p4 = new Phase(Phase.DISPATCH, 100);
        
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(p4));
    }
     
     public void testSort()
        throws Exception
    {
        Phase p1 = new Phase(Phase.TRANSPORT, 100);
        Phase p2 = new Phase(Phase.TRANSPORT, 100);

        assertEquals(0, p1.compareTo(p2));

        Phase p3 = new Phase(Phase.TRANSPORT, 400);
        Phase p4 = new Phase(Phase.DISPATCH, 60);

        assertEquals(-1, p1.compareTo(p3));
        assertEquals(1, p1.compareTo(p4));
    }
}