package org.codehaus.xfire.handler;

import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * <p>
 * A handler is just something that processes an XML message.
 * </p>
 * <p>
 * If an exception occurrs in the invoke method, the entity which
 * started the invocation, is responsible for turning the exception
 * into a fault.
 * </p>
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Feb 18, 2004
 */
public interface Handler
{
    String ROLE = Handler.class.getName();

    /**
     * @return null or an empty array if there are no headers.
     */
    QName[] getUnderstoodHeaders();

    /**
     * The roles which this service applies to.
     * 
     * @return <code>null</code> or an empty if this endpoint handles no
     *         roles.
     */
    String[] getRoles();

    /**
     * The phase which this handler would like to be in.
     * 
     * @return
     * @see Phase
     */
    String getPhase();
    
    /**
     * Invoke a handler. If a fault occurs it will be handled via the
     * <code>handleFault</code> method.
     * 
     * @param message
     *            The message context.
     */
    void invoke(MessageContext context)
        throws Exception;

    /**
     * Handles faults that occur in this handler. This is not responsible for
     * actually writing the fault response message.
     * 
     * @param context
     */
    void handleFault(XFireFault fault, MessageContext context);
    
    public List getAfter();
    
    public List getBefore();
}
