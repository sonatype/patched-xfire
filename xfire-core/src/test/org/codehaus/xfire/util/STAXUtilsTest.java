package org.codehaus.xfire.util;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.stax.DepthXMLStreamReader;
import org.jdom.Document;

import com.bea.xml.stream.MXParserFactory;
import com.bea.xml.stream.XMLOutputFactoryBase;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class STAXUtilsTest
    extends AbstractXFireTest
{
    private XMLInputFactory ifactory;
    private XMLOutputFactory ofactory;
    
    public void testFactoryCreation()
    {
        MessageContext ctx = new MessageContext();
        ctx.setProperty(XFire.STAX_INPUT_FACTORY, WstxInputFactory.class.getName());
        
        XMLStreamReader reader = STAXUtils.createXMLStreamReader(getResourceAsStream("amazon.xml"), null, ctx);
        
        ctx.setProperty(XFire.STAX_OUTPUT_FACTORY, WstxOutputFactory.class.getName());
        
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(new ByteArrayOutputStream(), null, ctx);
    }
    
    public void testWSTX() throws Exception
    {
        ifactory = new WstxInputFactory();
        ofactory = new WstxOutputFactory();

        doCopy();
        doSkipTest();
        doNameSpaceDoc();
        doAmazonDoc();
        doEbayDoc();
        doAmazonDoc2();
        doDOMWrite();
        doDOMWrite2();
        doDOMRead();
    }
    
    public void testRI() throws Exception
    {
        ifactory = new MXParserFactory();
        ofactory = new XMLOutputFactoryBase();
        
        doCopy();
        doSkipTest();
        doNameSpaceDoc();
        doAmazonDoc();
        doEbayDoc();
        doAmazonDoc2();
        doDOMWrite();
        doDOMWrite2();
        doDOMRead();
    }
    
    public void doCopy() throws Exception
    {
        String in = new String("<hello xmlns=\"\">world</hello>");
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamReader reader = ifactory.createXMLStreamReader(new StringReader(in));
        XMLStreamWriter writer = ofactory.createXMLStreamWriter(out);
        STAXUtils.copy(reader, writer);
        writer.close();
        out.close();
        System.out.println(out.toString());
        assertEquals(in, out.toString());
    }
    
    public void doSkipTest() throws Exception
    {
        XMLStreamReader reader = STAXUtils.createXMLStreamReader(getClass().getResourceAsStream("/org/codehaus/xfire/util/nowhitespace.xml"),null,null);
        //XMLStreamReader reader = ifactory.createXMLStreamReader(getClass().getResourceAsStream("/org/codehaus/xfire/util/nowhitespace.xml"));
        
        DepthXMLStreamReader dr = new DepthXMLStreamReader(reader);
        STAXUtils.toNextElement(dr);
        assertEquals("Envelope", dr.getLocalName());
        dr.next();
        STAXUtils.toNextElement(dr);
        assertEquals("Header", dr.getLocalName());
    }
    
    public void doAmazonDoc() throws Exception
    {
        String outS = doCopy("amazon.xml");
        
        Document doc = readDocument(outS, ifactory);
        
        addNamespace("a", "http://xml.amazon.com/AWSECommerceService/2004-08-01");
        assertValid("/a:ItemLookup", doc);
        assertValid("/a:ItemLookup/a:Request/a:IdType", doc);
    }

    public void doEbayDoc() throws Exception
    {
        String outS = doCopy("ebay.xml");
        
        Document doc = readDocument(outS, ifactory);
        
        addNamespace("e", "urn:ebay:api:eBayAPI");
        addNamespace("ebase", "urn:ebay:apis:eBLBaseComponents");
        assertValid("//ebase:Version", doc);
        assertValid("//ebase:ErrorLanguage", doc);
        assertValid("//e:UserID", doc);
    }
    
    public void doAmazonDoc2() throws Exception
    {
        String outS = doCopy("amazon2.xml");
        
        Document doc = readDocument(outS, ifactory);
        
        addNamespace("a", "http://webservices.amazon.com/AWSECommerceService/2004-10-19");
        assertValid("//a:ItemLookupResponse", doc);
        assertValid("//a:ItemLookupResponse/a:Items", doc);
    }
    
    public void doNameSpaceDoc() throws Exception {
        String outS = doCopy("namespacedoc.xml");
        
        Document doc = readDocument(outS, ifactory);
        
        addNamespace("a", "http://www.paraware.com/2005/PriceAndAvailabilityCheckResponse");
        addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        assertValid("//a:Header", doc);
        assertValid("//a:Header/a:Reference/a:RefNum", doc);
        assertValid("//a:Body/a:PartNumbers/a:UPC/@xsi:nil", doc);
    }
    

    /**
     * @return
     * @throws FactoryConfigurationError
     * @throws XMLStreamException
     */
    private String doCopy(String resource) throws FactoryConfigurationError, XMLStreamException
    {
        XMLStreamReader reader = ifactory.createXMLStreamReader(getClass().getResourceAsStream(resource));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = ofactory.createXMLStreamWriter(out);
        
        writer.writeStartDocument();
        STAXUtils.copy(reader, writer);
        writer.writeEndDocument();
        
        writer.close();
        String outS = out.toString();
        
        return outS;
    }
    
    public void doDOMWrite() throws Exception
    {
        org.w3c.dom.Document doc = DOMUtils.readXml(getResourceAsStream("amazon.xml"));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = ofactory.createXMLStreamWriter(bos);
        
        STAXUtils.writeElement(doc.getDocumentElement(), writer, false);
        
        writer.close();
        
        Document testDoc = readDocument(bos.toString(), ifactory);
        addNamespace("a", "http://xml.amazon.com/AWSECommerceService/2004-08-01");
        assertValid("//a:ItemLookup", testDoc);
        assertValid("//a:ItemLookup/a:Request", testDoc);
    }
    
    public void doDOMWrite2() throws Exception
    {
        org.w3c.dom.Document doc = DOMUtils.readXml(getResourceAsStream("nowhitespace.xml"));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = ofactory.createXMLStreamWriter(bos);
        
        STAXUtils.writeElement(doc.getDocumentElement(), writer, false);
        
        writer.close();
        
        Document testDoc = readDocument(bos.toString(), ifactory);
    }
    
    public void doDOMRead() throws Exception
    {
        XMLStreamReader reader = ifactory.createXMLStreamReader(getResourceAsStream("amazon2.xml"));
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(true);
        
        org.w3c.dom.Document doc = STAXUtils.read(dbf.newDocumentBuilder(), reader, false);

//        Diff diff = new Diff(DOMUtils.readXml(getResourceAsStream("amazon2.xml")), doc);
//        assertTrue("XML isn't similar: " + diff.toString(), diff.similar());
//        assertTrue(diff.identical());
    }
}
