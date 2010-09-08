package org.codehaus.xfire.util;

import java.util.Random;

/**
 * @author Hani Suleiman
 *         Date: Jun 10, 2005
 *         Time: 3:20:28 PM
 */
public class UID
{
  private static int counter;
  private static Random random = new Random(System.currentTimeMillis());
  
  public static String generate()
  {
    return String.valueOf(System.currentTimeMillis()) + counter++ + random.nextInt();
  }
}
