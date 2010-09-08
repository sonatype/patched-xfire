package org.codehaus.xfire;

/**
 * @author Arjen Poutsma
 */

import junit.framework.TestCase;

public class XFireFactoryTest
        extends TestCase
{
    public void testRegisterFactory()
            throws Exception
    {
        XFireFactory.registerFactory(XFireFactory.class, false);
    }
}