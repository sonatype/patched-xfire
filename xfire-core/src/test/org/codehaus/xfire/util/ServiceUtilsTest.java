package org.codehaus.xfire.util;

/**
 * @author Arjen Poutsma
 */

import junit.framework.TestCase;

public class ServiceUtilsTest
        extends TestCase
{

    public void testMakeServiceNameFromClassName()
            throws Exception
    {
        String result = ServiceUtils.makeServiceNameFromClassName(getClass());
        assertEquals("ServiceUtilsTest", result);
    }
}