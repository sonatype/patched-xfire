package org.codehaus.xfire;


/**
 * A catchable non-runtime exception
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 14, 2004
 */
public class XFireException
    extends Exception
{
    protected XFireException()
    {
    }

    /**
     * Constructs a new XFireException with the specified detail message.
     *
     * @param message the detail message.
     */
    public XFireException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new XFireException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public XFireException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
