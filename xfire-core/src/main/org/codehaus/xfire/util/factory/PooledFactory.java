package org.codehaus.xfire.util.factory;

/**
 * This class decorates a Factory object that uses a Pool strategy to cache the
 * factory result;
 * <p>
 * 
 * @author Ben Yu Feb 2, 2006 11:57:12 AM
 */
public class PooledFactory
    implements Factory
{
    private final Factory factory;

    private final Pool pool;

    public Object create()
        throws Throwable
    {
        return pool.getInstance(factory);
    }

    public PooledFactory(Factory factory, Pool pool)
    {
        this.factory = factory;
        this.pool = pool;
    }
}
