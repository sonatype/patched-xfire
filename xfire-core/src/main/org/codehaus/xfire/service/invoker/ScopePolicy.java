package org.codehaus.xfire.service.invoker;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.util.factory.Factory;

/**
 * This interface represents a scoping policy that caches servant instances
 * created by a Factory.
 * <p>
 * 
 * @author Ben Yu Feb 6, 2006 12:47:38 PM
 */
public interface ScopePolicy
{
    /**
     * Apply scope policy to a Factory object so that the instance created by
     * the Factory object can be cached properly.
     * 
     * @param f
     *            the Factory object.
     * @param ctxt
     *            the MessageContext object.
     * @return the Factory object that honors the scope.
     */
    Factory applyScope(Factory f, MessageContext ctxt);
}
