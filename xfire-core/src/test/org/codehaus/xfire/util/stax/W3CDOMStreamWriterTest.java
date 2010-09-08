package org.codehaus.xfire.util.stax;

import java.util.Iterator;

import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;


/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class W3CDOMStreamWriterTest
    extends AbstractStreamReaderTest
{
    public void testElementChild() throws Exception
    {
        W3CDOMStreamWriter writer = new W3CDOMStreamWriter();
        writer.writeStartDocument();
        writer.writeStartElement("root");
        writer.writeNamespace("xsi", SoapConstants.XSI_NS);
        
        writer.writeStartElement("urn:test", "child");
        
        assertEquals("xsi", writer.getPrefix(SoapConstants.XSI_NS));
        
        assertEquals(SoapConstants.XSI_NS, writer.getNamespaceContext().getNamespaceURI("xsi"));
        
        Iterator prefixes = writer.getNamespaceContext().getPrefixes(SoapConstants.XSI_NS);
        assertTrue(prefixes.hasNext());
        assertEquals("xsi", prefixes.next());
        assertFalse(prefixes.hasNext());
        
        writer.writeAttribute("ns1", SoapConstants.XSI_NS, "nil", "true");
        
        writer.writeEndElement();
        writer.writeEndElement();
        
        writer.writeEndDocument();
        
        Document doc = writer.getDocument();
        DOMUtils.writeXml(doc, System.out);
    }
}
