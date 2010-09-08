package org.codehaus.xfire.service.invoker;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.util.factory.Factory;
import org.codehaus.xfire.util.factory.Pool;
import org.codehaus.xfire.util.factory.PooledFactory;
import org.codehaus.xfire.util.factory.SingletonPool;

/**
 * This scope policy implements one servant instance per service.
 * <p>
 * 
 * @author Ben Yu Feb 6, 2006 11:38:08 AM
 */
public class ApplicationScopePolicy
    implements ScopePolicy
{
    public Factory applyScope(Factory f, MessageContext ctxt)
    {
        return new PooledFactory(f, pool);
    }

    public String toString()
    {
        return "application scope";
    }

    private final Pool pool = new SingletonPool();

    public static ScopePolicy instance()
    {
        return new ApplicationScopePolicy();
    }
}
