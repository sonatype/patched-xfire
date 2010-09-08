package org.codehaus.xfire.util.factory;

/**
 * Represents a pooling strategy that pools the data in a variable that's global
 * to all threads. <br>
 * This implementation is thread-safe.
 *
 * @author Ben Yu
 * 
 */
public class SingletonPool
    extends CachingPool
{
    public synchronized Object getInstance(Factory factory)
        throws Throwable
    {
        return super.getInstance(factory);
    }

    public synchronized Object getPooledInstance(Object def)
    {
        return super.getPooledInstance(def);
    }

    public synchronized boolean isPooled()
    {
        return super.isPooled();
    }
}
