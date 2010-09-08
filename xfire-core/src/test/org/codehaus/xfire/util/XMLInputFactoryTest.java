package org.codehaus.xfire.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.test.AbstractXFireTest;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 *  @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class XMLInputFactoryTest
    extends AbstractXFireTest
{

    public void testFactoryConfig()
        throws Exception
    {
        String xml = "<root><foo><![CDATA[data]]></foo></root>";

        MessageContext ctx = new MessageContext();
        ctx.setProperty(XFire.STAX_INPUT_FACTORY, MyInputFactory.class.getName());
        ctx.setProperty(XMLInputFactory.IS_COALESCING, "false");
        XMLStreamReader xmlReader = STAXUtils.createXMLStreamReader(new StringReader(xml), ctx);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        XMLStreamWriter xmlWriter = STAXUtils.createXMLStreamWriter(outStream, null, null);
        STAXUtils.copy(xmlReader, xmlWriter);
        xmlWriter.close();
        xmlReader.close();
        outStream.close();
        System.out.println(outStream.toString());
        String result = outStream.toString();
        assertTrue(result.indexOf("CDATA") > 0);
    }

    public static class MyInputFactory extends XMLInputFactory
    {
        XMLInputFactory xif = WstxInputFactory.newInstance();
        
        public XMLEventReader createFilteredReader(XMLEventReader arg0, EventFilter arg1)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLStreamReader createFilteredReader(XMLStreamReader arg0, StreamFilter arg1)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLEventReader createXMLEventReader(InputStream arg0, String arg1)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLEventReader createXMLEventReader(InputStream arg0)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLEventReader createXMLEventReader(Reader arg0)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLEventReader createXMLEventReader(Source arg0)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLEventReader createXMLEventReader(String arg0, InputStream arg1)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLEventReader createXMLEventReader(String arg0, Reader arg1)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLEventReader createXMLEventReader(XMLStreamReader arg0)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLStreamReader createXMLStreamReader(InputStream arg0, String arg1)
            throws XMLStreamException
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLStreamReader createXMLStreamReader(InputStream arg0)
            throws XMLStreamException
        {
            return xif.createXMLStreamReader(arg0);
        }

        public XMLStreamReader createXMLStreamReader(Reader arg0)
            throws XMLStreamException
        {
            return xif.createXMLStreamReader(arg0);
        }

        public XMLStreamReader createXMLStreamReader(Source arg0)
            throws XMLStreamException
        {
            return xif.createXMLStreamReader(arg0);
        }

        public XMLStreamReader createXMLStreamReader(String arg0, InputStream arg1)
            throws XMLStreamException
        {
            return xif.createXMLStreamReader(arg0, arg1);
        }

        public XMLStreamReader createXMLStreamReader(String arg0, Reader arg1)
            throws XMLStreamException
        {
            return xif.createXMLStreamReader(arg0, arg1);
        }

        public XMLEventAllocator getEventAllocator()
        {
            // TODO Auto-generated method stub
            return null;
        }

        public Object getProperty(String arg0)
            throws IllegalArgumentException
        {
            return xif.getProperty(arg0);
        }

        public XMLReporter getXMLReporter()
        {
            // TODO Auto-generated method stub
            return null;
        }

        public XMLResolver getXMLResolver()
        {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isPropertySupported(String arg0)
        {
            // TODO Auto-generated method stub
            return false;
        }

        public void setEventAllocator(XMLEventAllocator arg0)
        {
            // TODO Auto-generated method stub
            
        }

        public void setProperty(String arg0, Object arg1)
            throws IllegalArgumentException
        {
            xif.setProperty(arg0, arg1);
        }

        public void setXMLReporter(XMLReporter arg0)
        {
            // TODO Auto-generated method stub
            
        }

        public void setXMLResolver(XMLResolver arg0)
        {
            // TODO Auto-generated method stub
            
        }
        
    }
}
