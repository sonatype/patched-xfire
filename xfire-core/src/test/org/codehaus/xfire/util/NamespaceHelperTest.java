package org.codehaus.xfire.util;

import junit.framework.TestCase;

public class NamespaceHelperTest
        extends TestCase
{

    public void testMakeNamespaceFromClassName()
            throws Exception
    {
        String className = "org.codehaus.xfire.services.Echo";
        String namespace = NamespaceHelper.makeNamespaceFromClassName(className, "http");
        assertNotNull(namespace);
        assertEquals("Invalid namespace", "http://services.xfire.codehaus.org", namespace);
    }
}