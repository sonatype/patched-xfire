package org.codehaus.xfire.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Represents the base class for containers of <code>MessagePartInfo</code> objects.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public abstract class MessagePartContainer
{
    /**
     * The operation that this <code>MessageInfo</code> is a part of.
     */
    private OperationInfo operation;
    private Map messageParts = new HashMap();
    private List messagePartList = new LinkedList();

    /**
     * Initializes a new instance of the <code>MessagePartContainer</code>.
     *
     * @param operation the operation.
     */
    protected MessagePartContainer(OperationInfo operation)
    {
        this.operation = operation;
    }

    /**
     * Returns the operation of this container.
     *
     * @return the operation.
     */
    public OperationInfo getOperation()
    {
        return operation;
    }

    /**
     * Adds an message part to this conainer.
     *
     * @param name  the qualified name of the message part.
     * @param clazz the type of the message part.
     */
    public MessagePartInfo addMessagePart(QName name, Class clazz)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Invalid name [ null ]");
        }
        /*
        if (messageParts.containsKey(name))
        {
            throw new IllegalArgumentException(
                    "An message part with name [" + name + "] already exists in this container");
        }*/

        MessagePartInfo part = new MessagePartInfo(name, clazz, this);
        addMessagePart(part);
        return part;
    }
    
    /**
     * Adds an message part to this container.
     *
     * @param part the message part.
     */
    public void addMessagePart(MessagePartInfo part)
    {
        messageParts.put(part.getName(), part);
        messagePartList.add(part);
    }

    public int getMessagePartIndex(MessagePartInfo part)
    {
        return messagePartList.indexOf(part);
    }

    /**
     * Removes an message part from this container.
     *
     * @param name the qualified message part name.
     */
    public void removeMessagePart(QName name)
    {
        MessagePartInfo messagePart = getMessagePart(name);
        if (messagePart != null)
        {
            messageParts.remove(name);
            messagePartList.remove(messagePart);
        }
    }
    
    /**
     * Returns the message part with the given name, if found.
     *
     * @param name the qualified name.
     * @return the message part; or <code>null</code> if not found.
     */
    public MessagePartInfo getMessagePart(QName name)
    {
        return (MessagePartInfo) messageParts.get(name);
    }
    
    /**
     * Returns all message parts for this message.
     *
     * @return all message parts.
     */
    public List getMessageParts()
    {
        return Collections.unmodifiableList(messagePartList);
    }
    
    public int size()
    {
        return messagePartList.size();
    }
}
