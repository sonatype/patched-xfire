package org.codehaus.xfire.util.factory;

/**
 * A thread-unsafe implementation of Pool that does simple caching.
 * <p>
 * 
 * @author Ben Yu Feb 2, 2006 12:13:08 PM
 */
public class CachingPool
    implements Pool
{
    private transient Object v = null;

    private transient boolean pooled = false;

    private void readObject(java.io.ObjectInputStream in)
        throws ClassNotFoundException, java.io.IOException
    {
        in.defaultReadObject();
        this.pooled = false;
    }

    public Object getInstance(Factory factory)
        throws Throwable
    {
        if (!pooled)
        {
            v = factory.create();
            pooled = true;
        }
        return v;
    }

    public Object getPooledInstance(Object def)
    {
        return pooled ? v : def;
    }

    /**
     * Is this pool currently having something in cache?
     */
    public boolean isPooled()
    {
        return pooled;
    }
}
