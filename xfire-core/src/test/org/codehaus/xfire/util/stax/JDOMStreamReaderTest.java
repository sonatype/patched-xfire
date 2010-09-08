package org.codehaus.xfire.util.stax;

import javax.xml.stream.XMLStreamReader;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class JDOMStreamReaderTest
    extends AbstractStreamReaderTest
{
    public void testSingleElement() throws Exception
    {
        Element e = new Element("root", "urn:test");
        System.out.println("start: " + XMLStreamReader.START_ELEMENT);
        System.out.println("attr: " + XMLStreamReader.ATTRIBUTE);
        System.out.println("ns: " + XMLStreamReader.NAMESPACE);
        System.out.println("chars: " + XMLStreamReader.CHARACTERS);
        System.out.println("end: " + XMLStreamReader.END_ELEMENT);
        
        JDOMStreamReader reader = new JDOMStreamReader(e);
        testSingleElement(reader);
    }
    
    public void testTextChild() throws Exception
    {
        Element e = new Element("root", "urn:test");
        e.addContent("Hello World");
        
        JDOMStreamReader reader = new JDOMStreamReader(e);
        testTextChild(reader);
    }

    public void testAttributes() throws Exception
    {
        Element e = new Element("root", "urn:test");
        e.setAttribute(new Attribute("att1", "value1"));
        e.setAttribute(new Attribute("att2",  "value2", Namespace.getNamespace("p", "urn:test2")));
        
        JDOMStreamReader reader = new JDOMStreamReader(e);
        testAttributes(reader);
    }
    
    public void testElementChild() throws Exception
    {
        Element e = new Element("root", "urn:test");
        Element child = new Element("child", "a", "urn:test2");
        e.addContent(child);
        
        JDOMStreamReader reader = new JDOMStreamReader(e);
        testElementChild(reader);
    }
}
