package org.codehaus.xfire.service.invoker;

import java.beans.PropertyEditorSupport;

/**
 * This class is responsible for converting string to ScopePolicy object.
 * <p>
 * 
 * @author Ben Yu Feb 12, 2006 12:40:31 AM
 */
public class ScopePolicyEditor
    extends PropertyEditorSupport
{
    /**
     * To get the default scope policy when no policy is specified. This
     * implementation uses "application" as default.
     */
    public static ScopePolicy getDefaultScope()
    {
        return ApplicationScopePolicy.instance();
    }

    /**
     * Convert a policy name to ScopePolicy object.
     * 
     * @param policy
     *            the policy name.
     * @return the ScopePolicy object.
     */
    public static ScopePolicy toScopePolicy(String policy)
    {
        if (policy == null)
        {
            return getDefaultScope();
        }
        
        policy = policy.trim();
        
        if (policy.length() == 0)
        {
            return getDefaultScope();
        }
        else if ("application".equals(policy))
        {
            return ApplicationScopePolicy.instance();
        }
        else if ("session".equals(policy))
        {
            return SessionScopePolicy.instance();
        }
        else if ("request".equals(policy))
        {
            return RequestScopePolicy.instance();
        }
        else
        {
            throw new IllegalArgumentException("Scope " + policy + " is invalid.");
        }
    }

    public void setAsText(String text)
    {
        setValue(toScopePolicy(text));
    }
}
