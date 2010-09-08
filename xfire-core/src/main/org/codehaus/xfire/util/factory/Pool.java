package org.codehaus.xfire.util.factory;

/**
 * Represents a pooling strategy for certain object instance.
 * 
 * @author Ben Yu
 */
public interface Pool
    extends java.io.Serializable
{
    /**
     * Apply the pooling strategy and return an instance from either the pool or
     * the factory.
     * 
     * @param factory
     *            the factory to create the object instance.
     * @return the object instance.
     */
    Object getInstance(Factory factory)
        throws Throwable;

    /**
     * Get the instance that's already pooled.
     * 
     * @param def
     *            the default value to return if there's no pooled instance.
     * @return the pooled instance or the default object.
     */
    Object getPooledInstance(Object def);

    /**
     * Is this pool currently having something in cache?
     */
    boolean isPooled();
}
