package org.codehaus.xfire;

import junit.framework.TestCase;

public class ContextTest extends TestCase
{
    public void testContext() {
        AbstractContext context = new AbstractContext() {};
        
        context.setProperty("hello", "world");
        assertEquals("world", context.getProperty("hello"));
        assertEquals("world", context.removeProperty("hello"));
        assertNull(context.getProperty("hello"));
    }
}
