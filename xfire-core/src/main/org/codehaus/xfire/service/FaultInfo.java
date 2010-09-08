package org.codehaus.xfire.service;

import java.util.Iterator;

import javax.xml.namespace.QName;

/**
 * Represents the description of a service operation fault.
 * <p/>
 * Faults are created using the {@link OperationInfo#addFault(String)} method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class FaultInfo
        extends MessagePartContainer
        implements Visitable
{
    private String name;
    private QName messageName;
    private Class exceptionClass;
    private String documentation;
    
    
    public String getDocumentation()
    {
        return documentation;
    }

    public void setDocumentation(String documentation)
    {
        this.documentation = documentation;
    }

    /**
     * Initializes a new instance of the <code>FaultInfo</code> class with the given name and operation
     *
     * @param name the name.
     */
    FaultInfo(String name, OperationInfo operation)
    {
        super(operation);
        this.name = name;
    }

    /**
     * Returns the name of the fault.
     *
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the fault.
     *
     * @param name the name.
     */
    public void setName(String name)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }
        getOperation().removeFault(getName());
        this.name = name;
        getOperation().addFault(this);
    }

    /**
     * Acceps the given visitor. Iterates over all message part infos.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.startFault(this);
        for (Iterator iterator = getMessageParts().iterator(); iterator.hasNext();)
        {
            MessagePartInfo messagePartInfo = (MessagePartInfo) iterator.next();
            messagePartInfo.accept(visitor);
        }
        visitor.endFault(this);
    }

    public Class getExceptionClass()
    {
        return exceptionClass;
    }

    public void setExceptionClass(Class exceptionClass)
    {
        this.exceptionClass = exceptionClass;
    }

    public QName getMessageName()
    {
        return messageName;
    }

    public void setMessageName(QName messageName)
    {
        this.messageName = messageName;
    }
    
}
