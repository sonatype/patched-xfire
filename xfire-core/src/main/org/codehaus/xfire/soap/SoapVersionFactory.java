package org.codehaus.xfire.soap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapVersionFactory
{
    private static SoapVersionFactory factory = new SoapVersionFactory();

    static
    {
        getInstance().register(Soap11.getInstance());
        getInstance().register(Soap12.getInstance());
    }
    
    private Map versions = new HashMap();
    
    public static SoapVersionFactory getInstance()
    {
        return factory;
    }
    
    public SoapVersion getSoapVersion(String namespace)
    {
        return (SoapVersion) versions.get(namespace);
    }
    
    public void register(SoapVersion version)
    {
        versions.put(version.getNamespace(), version);
    }

    public Iterator getVersions()
    {
        return versions.values().iterator();
    }
}
