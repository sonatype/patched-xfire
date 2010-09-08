package org.codehaus.xfire.service;

/**
 * Indicates that a class may be visited by a {@link Visitor}.
 * <p/>
 * Used to recurse into {@link ServiceInfo}, {@link OperationInfo}, {@link MessageInfo}, etc.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see Visitor
 */
public interface Visitable
{
    /**
     * Acceps the given visitor. Subclasses are required to call the <code>begin*</code> method on the given visitor,
     * iterate over their members, call {@link #accept} for each of them, and call <code>end*</code>.
     *
     * @param visitor the visitor.
     */
    void accept(Visitor visitor);
}
