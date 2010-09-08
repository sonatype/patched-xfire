package org.codehaus.xfire.util;

import java.lang.reflect.Method;
import java.util.Comparator;

public class MethodComparator
    implements Comparator
{

    public int compare(Object o1, Object o2)
    {
        Method m1 = (Method) o1;
        Method m2 = (Method) o2;
     
        int val = m1.getName().compareTo(m2.getName());
        if (val == 0)
        {
            val = m1.getParameterTypes().length - m2.getParameterTypes().length;
            if (val == 0)
            {
                Class[] types1 = m1.getParameterTypes();
                Class[] types2 = m2.getParameterTypes();
                for (int i = 0; i < types1.length; i++)
                {
                    val = types1[i].getName().compareTo(types2[i].getName());
                    
                    if (val != 0) break;
                }
            }
        }
        return val;
    }

}
