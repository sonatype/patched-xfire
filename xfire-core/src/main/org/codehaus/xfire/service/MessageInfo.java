package org.codehaus.xfire.service;

import java.util.Iterator;

import javax.xml.namespace.QName;


/**
 * Represents the description of a service operation message.
 * <p/>
 * Messages are created using the {@link OperationInfo#createMessage} method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class MessageInfo
        extends MessagePartContainer
        implements Visitable
{
    private QName name;

    /**
     * Initializes a new instance of the <code>MessageInfo</code> class with the given qualified name and operation.
     *
     * @param name      the name.
     * @param operation the operation.
     */
    MessageInfo(QName name, OperationInfo operation)
    {
        super(operation);
        this.name = name;
    }

    /**
     * Returns the qualified name of the message info.
     *
     * @return the name.
     */
    public QName getName()
    {
        return name;
    }

    /**
     * Sets the qualified name of the message info.
     *
     * @param name the qualified name.
     */
    public void setName(QName name)
    {
        this.name = name;
    }

    /**
     * Acceps the given visitor. Iterates over all message part infos.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.startMessage(this);
        for (Iterator iterator = getMessageParts().iterator(); iterator.hasNext();)
        {
            MessagePartInfo messagePartInfo = (MessagePartInfo) iterator.next();
            messagePartInfo.accept(visitor);
        }
        visitor.endMessage(this);
    }
}
