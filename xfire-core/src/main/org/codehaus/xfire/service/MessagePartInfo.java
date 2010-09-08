package org.codehaus.xfire.service;

import javax.xml.namespace.QName;

import org.codehaus.xfire.wsdl.SchemaType;


/**
 * Represents the description of a service operation message part.
 * <p/>
 * Message parts are created using the {@link MessageInfo#addMessagePart} or {@link FaultInfo#addMessagePart}  method.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class MessagePartInfo
        implements Visitable
{
    private QName name;
    private Class typeClass;
    private MessagePartContainer container;
    private SchemaType schemaType;
    private int index;
    private boolean schemaElement = true;
    private String documentation;
    private QName wrappedType;
    
    MessagePartInfo(QName name, Class typeClass, MessagePartContainer container)
    {
        this.name = name;
        this.typeClass = typeClass;
        this.container = container;
    }

    /**
     * @return Returns the name.
     */
    public QName getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(QName name)
    {
        this.name = name;
    }

    public Class getTypeClass()
    {
        return typeClass;
    }

    public void setTypeClass(Class typeClass)
    {
        this.typeClass = typeClass;
    }

    public MessagePartContainer getContainer()
    {
        return container;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }
    
    public SchemaType getSchemaType()
    {
        return schemaType;
    }

    public void setSchemaType(SchemaType schemaType)
    {
        this.schemaType = schemaType;
    }

    /**
     * Is this message part a concrete type declared in a schema? Or are
     * we referencing an abstract schema type?
     * 
     * @return True if this is a concrete type.
     */
    public boolean isSchemaElement()
    {
        return schemaElement;
    }
    

    public void setSchemaElement(boolean schemaElement)
    {
        this.schemaElement = schemaElement;
    }
    

    /**
     * Acceps the given visitor.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.startMessagePart(this);
        visitor.endMessagePart(this);
    }

    public String getDocumentation()
    {
        return documentation;
    }

    public void setDocumentation(String documentation)
    {
        this.documentation = documentation;
    }

	public QName getWrappedType() {
		return wrappedType;
	}

	public void setWrappedType(QName wrappedType) {
		this.wrappedType = wrappedType;
	}
    
}