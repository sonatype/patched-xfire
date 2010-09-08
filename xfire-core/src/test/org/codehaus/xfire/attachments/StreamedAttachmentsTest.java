package org.codehaus.xfire.attachments;

import java.io.FileInputStream;
import java.util.Iterator;

import javax.activation.DataHandler;

import org.codehaus.xfire.test.AbstractXFireTest;

public class StreamedAttachmentsTest
    extends AbstractXFireTest
{
    
    public void testText()
        throws Exception
    {
        StreamedAttachments atts = new StreamedAttachments(new FileInputStream(getTestFile("src/test/org/codehaus/xfire/attachments/mimedata")), 
                                                           "multipart/related; type=\"application/xop+xml\"; start=\"<soap.xml@xfire.codehaus.org>\"; start-info=\"null; charset=utf-8\"; boundary=\"----=_Part_4_701508.1145579811786\"");
        Attachment soapMessage = atts.getSoapMessage();
        assertNotNull(soapMessage);
        assertEquals("text/xml", soapMessage.getHeader("Content-Type"));
        DataHandler handler = soapMessage.getDataHandler();
        assertNotNull(handler);
        assertNotNull(handler.getInputStream());
        
        Iterator parts = atts.getParts();
        assertTrue(parts.hasNext());

        Attachment a = (Attachment) parts.next();
        assertEquals("image/jpeg", a.getHeader("Content-Type"));
        assertEquals("xfire_logo.jpg", a.getId());
        
        assertFalse(parts.hasNext());
    }
    
    public void testBoundaryWithoutQuotes()
        throws Exception
    {
        StreamedAttachments atts = new StreamedAttachments(new FileInputStream(getTestFile("src/test/org/codehaus/xfire/attachments/mimedata2")),
                                                           "start=\"<soap.xml@xfire.codehaus.org>\"; boundary=----=_Part_0_1696092.1145592699395");
        Attachment soapMessage = atts.getSoapMessage();
        assertNotNull(soapMessage);
        DataHandler handler = soapMessage.getDataHandler();
        assertNotNull(handler);
        assertNotNull(handler.getInputStream());
        
        Iterator parts = atts.getParts();
        assertTrue(parts.hasNext());
    
        Attachment a = (Attachment) parts.next();
    
        assertFalse(parts.hasNext());
    }
        
    public void testText2()
        throws Exception
    {
        StreamedAttachments atts = new StreamedAttachments(new FileInputStream(getTestFile("src/test/org/codehaus/xfire/attachments/mimedata2")),
                                                           "multipart/related; type=\"application/xop+xml\"; start=\"<soap.xml@xfire.codehaus.org>\"; start-info=\"null; charset=utf-8\"; boundary=\"----=_Part_0_1696092.1145592699395\"");
        Attachment soapMessage = atts.getSoapMessage();
        assertNotNull(soapMessage);
        DataHandler handler = soapMessage.getDataHandler();
        assertNotNull(handler);
        assertNotNull(handler.getInputStream());
        
        Iterator parts = atts.getParts();
        assertTrue(parts.hasNext());
    
        Attachment a = (Attachment) parts.next();

        assertFalse(parts.hasNext());
    }
}
