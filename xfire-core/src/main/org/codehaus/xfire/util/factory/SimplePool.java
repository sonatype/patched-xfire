package org.codehaus.xfire.util.factory;

/**
 * A simple implementation of Pool that uses null to indicate non-existent pool
 * entry.
 * <p>
 * This implementation synchronizes on {@link #getMutex()} for thread safety.
 * </p>
 * 
 * @author Ben Yu Feb 2, 2006 3:14:45 PM
 */
public abstract class SimplePool
    implements Pool
{
    public Object getInstance(Factory factory)
        throws Throwable
    {
        synchronized (getMutex())
        {
            Object ret = get();
            if (ret == null)
            {
                ret = factory.create();
                set(ret);
            }
            return ret;
        }
    }

    public Object getPooledInstance(Object def)
    {
        synchronized (getMutex())
        {
            return ifnull(get(), def);
        }
    }

    public boolean isPooled()
    {
        synchronized (getMutex())
        {
            return get() != null;
        }
    }

    protected static Object ifnull(Object obj, Object def)
    {
        return obj == null ? def : obj;
    }

    /**
     * Get the pooled instance. null if not found.
     * 
     * @return the pooled instance.
     */
    public abstract Object get();

    /**
     * set an value to the pool.
     * 
     * @param val
     *            the value to be pooled.
     */
    public abstract void set(Object val);

    /**
     * Get the object that can be used to synchronize.
     */
    protected abstract Object getMutex();
}
