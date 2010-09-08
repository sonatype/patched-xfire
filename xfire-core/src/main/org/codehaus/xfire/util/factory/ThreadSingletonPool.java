package org.codehaus.xfire.util.factory;

/**
 * Represents a pooling strategy that pools the data into a ThreadLocal object.
 * 
 * @author Ben Yu
 */
public class ThreadSingletonPool
    implements Pool
{
    private static final class ThreadLocalCache
        extends ThreadLocal
    {
        protected Object initialValue()
        {
            return new CachingPool();
        }

        CachingPool getPool()
        {
            return (CachingPool) this.get();
        }
    }

    private transient ThreadLocalCache cache = new ThreadLocalCache();

    private void readObject(java.io.ObjectInputStream in)
        throws ClassNotFoundException, java.io.IOException
    {
        in.defaultReadObject();
        cache = new ThreadLocalCache();
    }

    public Object getInstance(Factory factory)
        throws Throwable
    {
        return cache.getPool().getInstance(factory);
        /*
         * Object v = cache.get(); if(v==null){ v = factory.create();
         * cache.set(v); } return v;
         */
    }

    public Object getPooledInstance(Object def)
    {
        return cache.getPool().getPooledInstance(def);
    }

    public boolean isPooled()
    {
        return cache.getPool().isPooled();
    }
}
