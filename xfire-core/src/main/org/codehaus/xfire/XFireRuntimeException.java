package org.codehaus.xfire;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Used for internal XFire exceptions when a fault shouldn't be returned to the service invoker.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 14, 2004
 */
public class XFireRuntimeException
        extends RuntimeException
{
    private Throwable cause;
    private String message;
    
    /**
     * Constructs a new xfire runtime exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public XFireRuntimeException(String message)
    {
        super(message);
        this.message = message;
    }

    /**
     * Constructs a new xfire runtime exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public XFireRuntimeException(String message, Throwable cause)
    {
        super(message);
        this.message = message;
        this.cause = cause;
    }


    /**
     * Returns the cause of this throwable or <code>null</code> if the  cause is nonexistent or unknown.
     *
     * @return the nested cause.
     */
    public Throwable getCause()
    {
        return (this.cause == this ? null : this.cause);
    }

    /**
     * Return the detail message, including the message from the {@link #getCause() nested exception} if there is one.
     *
     * @return the detail message.
     */
    public String getMessage()
    {
        if (this.cause == null || this.cause == this)
        {
            return message;
        }
        else
        {
            return message + ". Nested exception is " + this.cause.getClass().getName() +
                    ": " + this.cause.getMessage();
        }
    }

    public String getActualMessage()
    {
        return message;
    }
    
    /**
     * Prints this throwable and its backtrace to the specified print stream.
     *
     * @param s <code>PrintStream</code> to use for output
     */
    public void printStackTrace(PrintStream s)
    {
        if (this.cause == null || this.cause == this)
        {
            super.printStackTrace(s);
        }
        else
        {
            s.println(this);
            this.cause.printStackTrace(s);
        }
    }

    /**
     * Prints this throwable and its backtrace to the specified print writer.
     *
     * @param w <code>PrintWriter</code> to use for output
     */
    public void printStackTrace(PrintWriter w)
    {
        if (this.cause == null || this.cause == this)
        {
            super.printStackTrace(w);
        }
        else
        {
            w.println(this);
            this.cause.printStackTrace(w);
        }
    }

    public void prepend(String message)
    {
        if(this.message != null)
        {
            this.message = message + ": " + this.message;
        }
        else
        {
            this.message = message;
        }
    }
    
    public void setMessage(String s)
    {
        message = s;
    }
}
