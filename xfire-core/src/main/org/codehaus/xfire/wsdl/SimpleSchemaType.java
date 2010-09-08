package org.codehaus.xfire.wsdl;

import java.util.Set;

import javax.xml.namespace.QName;

import org.jdom.Element;

public class SimpleSchemaType
    implements SchemaType
{
    private boolean complex;
    private boolean _abstract;
    private boolean nillable;
    private Set dependencies;
    private QName schemaType;
    private boolean writeOuter;
    
    public void writeSchema(Element element)
    {
    }
    
    public boolean isWriteOuter()
    {
        return writeOuter;
    }
    
    public void setWriteOuter(boolean writeOuter)
    {
        this.writeOuter = writeOuter;
    }

    public boolean isAbstract()
    {
        return _abstract;
    }

    public void setAbstract(boolean _abstract)
    {
        this._abstract = _abstract;
    }

    public boolean isComplex()
    {
        return complex;
    }

    public void setComplex(boolean complex)
    {
        this.complex = complex;
    }

    public Set getDependencies()
    {
        return dependencies;
    }

    public void setDependencies(Set dependencies)
    {
        this.dependencies = dependencies;
    }

    public QName getSchemaType()
    {
        return schemaType;
    }

    public void setSchemaType(QName schemaType)
    {
        this.schemaType = schemaType;
    }

    public boolean isNillable()
    {
        return nillable;
    }

    public void setNillable(boolean nillable)
    {
        this.nillable = nillable;
    }
}
