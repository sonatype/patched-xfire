package org.codehaus.xfire.util.factory;

/**
 * Represents an object factory.
 * <p>
 * 
 * @author Ben Yu Jan 6, 2006 12:38:18 AM
 */
public interface Factory
{
    Object create() throws Throwable;
}
