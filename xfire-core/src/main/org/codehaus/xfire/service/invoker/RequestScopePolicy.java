package org.codehaus.xfire.service.invoker;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.util.factory.Factory;

/**
 * This scope policy implements one servant instance per request.
 * <p>
 * 
 * @author Ben Yu Feb 6, 2006 11:38:08 AM
 */
public class RequestScopePolicy
    implements ScopePolicy
{
    public Factory applyScope(Factory f, MessageContext ctxt)
    {
        return f;
    }

    private static final RequestScopePolicy singleton = new RequestScopePolicy();

    public static ScopePolicy instance()
    {
        return singleton;
    }
}
