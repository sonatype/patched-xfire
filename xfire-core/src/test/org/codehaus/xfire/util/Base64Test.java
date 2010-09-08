package org.codehaus.xfire.util;

/**
 * @author Arjen Poutsma
 */

import junit.framework.TestCase;

public class Base64Test
        extends TestCase
{

    public void testEncodeDecode()
    {
        byte[] input = new byte[]{0x1, 0x2};
        String result = Base64.encode(input);
        assertNotNull(result);
        assertTrue(result.length() > 0);

        byte[] output = Base64.decode(result);
        assertNotNull(output);
        assertEquals(input.length, output.length);
        for (int i = 0; i < input.length; i++)
        {
            assertEquals("Encode/decode invalid", input[i], output[i]);
        }
    }
}