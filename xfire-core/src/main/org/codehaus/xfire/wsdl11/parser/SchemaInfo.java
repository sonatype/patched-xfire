package org.codehaus.xfire.wsdl11.parser;

import javax.wsdl.Definition;

import org.apache.ws.commons.schema.XmlSchema;
import org.w3c.dom.Element;

public class SchemaInfo
{
    private Element schemaElement;
    private XmlSchema schema;
    private Definition definition;
    private boolean imported;
    
    public boolean isImported()
    {
        return imported;
    }
    public void setImported(boolean imported)
    {
        this.imported = imported;
    }
    public Definition getDefinition()
    {
        return definition;
    }
    public void setDefinition(Definition definition)
    {
        this.definition = definition;
    }
    public XmlSchema getSchema()
    {
        return schema;
    }
    public void setSchema(XmlSchema schema)
    {
        this.schema = schema;
    }
    public Element getSchemaElement()
    {
        return schemaElement;
    }
    public void setSchemaElement(Element schemaElement)
    {
        this.schemaElement = schemaElement;
    }
}
