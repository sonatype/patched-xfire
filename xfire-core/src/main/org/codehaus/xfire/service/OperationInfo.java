package org.codehaus.xfire.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.AbstractContext;

/**
 * Represents the description of a service operation. An operation has a name, and consists of a number of in and out
 * parameters.
 * <p/>
 * Operations are created using the {@link ServiceInfo#addOperation} method.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class OperationInfo
    extends AbstractContext
    implements Visitable
{
    private QName name;
    private ServiceInfo service;
    private String mep;
    private boolean async;
    private MessageInfo inputMessage;
    private MessageInfo outputMessage;
    // maps string names to FaultInfo objects
    private Map faults = new HashMap();
    private Method method;
    private String documenation;

    /**
     * Initializes a new instance of the <code>OperationInfo</code> class with the given name and service.
     *
     * @param name    the name of the operation.
     * @param service the service.
     */
    OperationInfo(String name, Method method, ServiceInfo service)
    {
        this(new QName(name), method, service);
    }
    
    OperationInfo(QName name, Method method, ServiceInfo service)
    {
        this.name = name;
        this.service = service;
        this.method = method;
    }

    /**
     * Returns the name of the operation.
     *
     * @return the name of the operation.
     */
    public String getName()
    {
        return name.getLocalPart();
    }

    public QName getQName()
    {
        return name;
    }

    /**
     * Sets the name of the operation.
     *
     * @param name the new name of the operation.
     */
    public void setName(String name)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }

        service.removeOperation(this.name);
        this.name = new QName(this.name.getNamespaceURI(), name);
        service.addOperation(this);
    }

    public Method getMethod()
    {
        return method;
    }

    /**
     * Whether or not the operation should be invoked asynchronously.
     * 
     * @return
     */
    public boolean isAsync()
    {
        return async;
    }

    public void setAsync(boolean async)
    {
        this.async = async;
    }

    /**
     * Get the message exchange pattern of this operation.
     * @return
     */
    public String getMEP()
    {
        return mep;
    }

    public void setMEP(String mep)
    {
        this.mep = mep;
    }

    /**
     * Returns the service descriptor of this operation.
     *
     * @return the service.
     */
    public ServiceInfo getService()
    {
        return service;
    }

    /**
     * Creates a new message. This message can be set as either {@link #setInputMessage(MessageInfo) input message} or
     * {@link #setOutputMessage(MessageInfo) output message}.
     *
     * @param name the name of the message.
     * @return the created message.
     */
    public MessageInfo createMessage(QName name)
    {
        MessageInfo message = new MessageInfo(name, this);
        return message;
    }

    /**
     * Returns the input message info.
     *
     * @return the input message info.
     */
    public MessageInfo getInputMessage()
    {
        return inputMessage;
    }

    /**
     * Sets the input message info.
     *
     * @param inputMessage the input message info.
     */
    public void setInputMessage(MessageInfo inputMessage)
    {
        this.inputMessage = inputMessage;
    }

    /**
     * Returns the output message info.
     *
     * @return the output message info.
     */
    public MessageInfo getOutputMessage()
    {
        return outputMessage;
    }

    /**
     * Sets the output message info.
     *
     * @param outputMessage the output message info.
     */
    public void setOutputMessage(MessageInfo outputMessage)
    {
        this.outputMessage = outputMessage;
    }

    /**
     * Adds an fault to this operation.
     *
     * @param name the fault name.
     */
    public FaultInfo addFault(String name)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Invalid name [" + name + "]");
        }
        if (faults.containsKey(name))
        {
            throw new IllegalArgumentException("A fault with name [" + name + "] already exists in this operation");
        }
        FaultInfo fault = new FaultInfo(name, this);
        addFault(fault);
        return fault;
    }

    /**
     * Adds a fault to this operation.
     *
     * @param fault the fault.
     */
    void addFault(FaultInfo fault)
    {
        faults.put(fault.getName(), fault);
    }

    /**
     * Removes a fault from this operation.
     *
     * @param name the qualified fault name.
     */
    public void removeFault(String name)
    {
        faults.remove(name);
    }

    /**
     * Returns the fault with the given name, if found.
     *
     * @param name the name.
     * @return the fault; or <code>null</code> if not found.
     */
    public FaultInfo getFault(String name)
    {
        return (FaultInfo) faults.get(name);
    }

    /**
     * Returns all faults for this operation.
     *
     * @return all faults.
     */
    public Collection getFaults()
    {
        return Collections.unmodifiableCollection(faults.values());
    }

    /**
     * Acceps the given visitor. Iterates over the input and output messages, if set.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.startOperation(this);
        if (inputMessage != null)
        {
            inputMessage.accept(visitor);
        }
        if (outputMessage != null)
        {
            outputMessage.accept(visitor);
        }
        for (Iterator iterator = faults.values().iterator(); iterator.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) iterator.next();
            faultInfo.accept(visitor);
        }
        visitor.endOperation(this);
    }

    public boolean hasOutput()
    {
        return outputMessage != null;
    }

    public boolean hasInput()
    {
        return inputMessage != null;
    }

    public String getDocumenation()
    {
        return documenation;
    }

    public void setDocumenation(String documenation)
    {
        this.documenation = documenation;
    }
    
}
