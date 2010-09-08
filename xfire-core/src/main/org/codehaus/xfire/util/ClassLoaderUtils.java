package org.codehaus.xfire.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This class is extremely useful for loading resources and classes in a fault tolerant manner
 * that works across different applications servers. Do not touch this unless you're a grizzled classloading
 * guru veteran who is going to verify any change on 6 different application servers.
 */
public class ClassLoaderUtils
{
    /**
     * Load a given resource.
     * <p/>
     * This method will try to load the resource using the following methods (in order):
     * <ul>
     * <li>From Thread.currentThread().getContextClassLoader()
     * <li>From ClassLoaderUtil.class.getClassLoader()
     * <li>callingClass.getClassLoader()
     * </ul>
     *
     * @param resourceName The name of the resource to load
     * @param callingClass The Class object of the calling object
     */
    public static URL getResource(String resourceName, Class callingClass)
    {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
    
        if(url == null)
        {
            url = ClassLoaderUtils.class.getClassLoader().getResource(resourceName);
        }
    
        if(url == null)
        {
            ClassLoader cl = callingClass.getClassLoader();
      
            if(cl != null)
            {
                url = cl.getResource(resourceName);
            }
        }
    
        if((url == null) && (resourceName != null) && (resourceName.charAt(0) != '/'))
        {
            return getResource('/' + resourceName, callingClass);
        }
    
        return url;
    }
  
    /**
     * This is a convenience method to load a resource as a stream.
     * <p/>
     * The algorithm used to find the resource is given in getResource()
     *
     * @param resourceName The name of the resource to load
     * @param callingClass The Class object of the calling object
     */
    public static InputStream getResourceAsStream(String resourceName, Class callingClass)
    {
        URL url = getResource(resourceName, callingClass);
    
        try
        {
            return (url != null) ? url.openStream() : null;
        }
        catch(IOException e)
        {
            return null;
        }
    }
  
    /**
     * Load a class with a given name.
     * <p/>
     * It will try to load the class in the following order:
     * <ul>
     * <li>From Thread.currentThread().getContextClassLoader()
     * <li>Using the basic Class.forName()
     * <li>From ClassLoaderUtil.class.getClassLoader()
     * <li>From the callingClass.getClassLoader()
     * </ul>
     *
     * @param className The name of the class to load
     * @param callingClass The Class object of the calling object
     * @throws ClassNotFoundException If the class cannot be found anywhere.
     */
    public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException
    {
        try
        {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            
            if (cl != null)
                return cl.loadClass(className);
            
            return loadClass2(className, callingClass);
        }
        catch(ClassNotFoundException e)
        {
            return loadClass2(className, callingClass);
        }
    }

    private static Class loadClass2(String className, Class callingClass)
        throws ClassNotFoundException
    {
        try
        {
            return Class.forName(className);
        }
        catch(ClassNotFoundException ex)
        {
            try
            {
                return ClassLoaderUtils.class.getClassLoader().loadClass(className);
            }
            catch(ClassNotFoundException exc)
            {
                return callingClass.getClassLoader().loadClass(className);
            }
        }
    }
}
