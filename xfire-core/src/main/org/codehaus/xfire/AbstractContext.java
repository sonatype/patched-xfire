package org.codehaus.xfire;

import java.util.HashMap;
import java.util.Map;

public class AbstractContext
{
    private Map properties = new HashMap();
 
    public Object getProperty(String key)
    {
        return properties.get(key);
    }

    public void setProperty(String key, Object value)
    {
        properties.put(key, value);
    }
    
    public Object removeProperty(String key)
    {
        return properties.remove(key);
    }
}
