package org.codehaus.xfire.wsdl11.builder;

import java.io.PrintWriter;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

public class SchemaSerializer
    implements ExtensionSerializer
{

    public void marshall(Class parentType,
                         QName elementType,
                         ExtensibilityElement extension,
                         PrintWriter pw,
                         Definition def,
                         ExtensionRegistry extReg)
        throws WSDLException
    {
        try
        {
            writeXml(((Schema) extension).getElement(), pw);
        }
        catch (TransformerException e)
        {
            throw new WSDLException("", "Could not write schema.", e);
        }
    }
    
    private void writeXml(Node n, PrintWriter pw)
        throws TransformerException
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.transform(new DOMSource(n), new StreamResult(pw));
    }
}
