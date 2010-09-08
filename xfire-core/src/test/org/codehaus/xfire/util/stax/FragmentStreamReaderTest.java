package org.codehaus.xfire.util.stax;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.STAXUtils;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class FragmentStreamReaderTest
    extends AbstractXFireTest
{
    private XMLInputFactory ifactory;
    private XMLOutputFactory ofactory;
    
    public void testReader() throws Exception
    {
        XMLInputFactory ifactory = STAXUtils.getXMLInputFactory(null);
        XMLStreamReader reader = 
            ifactory.createXMLStreamReader(getClass().getResourceAsStream("/org/codehaus/xfire/util/amazon.xml"));
        
        DepthXMLStreamReader dr = new DepthXMLStreamReader(reader);
        
        STAXUtils.toNextElement(dr);
        assertEquals("ItemLookup", dr.getLocalName());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.getEventType());
        
        FragmentStreamReader fsr = new FragmentStreamReader(dr);
        assertTrue(fsr.hasNext());
        
        assertEquals(XMLStreamReader.START_DOCUMENT, fsr.next());
        assertEquals(XMLStreamReader.START_DOCUMENT, fsr.getEventType());
        
        fsr.next();
        
        assertEquals("ItemLookup", dr.getLocalName());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.getEventType());
        
        fsr.close();
    }
}
