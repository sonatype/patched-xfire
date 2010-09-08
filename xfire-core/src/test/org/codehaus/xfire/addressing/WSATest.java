package org.codehaus.xfire.addressing;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.jdom.Document;
import org.jdom.Element;

public class WSATest
    extends AbstractXFireTest
{

    public void test200508Headers()
        throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder
                .build(getResourceAsStream("/org/codehaus/xfire/addressing/200508Headers1.xml"));

        AddressingHeadersFactory200508 factory = new AddressingHeadersFactory200508();

        assertTrue(factory.hasHeaders(doc.getRootElement()));

        AddressingHeaders headers = factory.createHeaders(doc.getRootElement());
        assertEquals("http://example.com/6B29FC40-CA47-1067-B31D-00DD010662DA", headers
                .getMessageID());

        assertNotNull(headers.getReplyTo());
        assertEquals("http://example.com/business/client1", headers.getReplyTo().getAddress());
        assertEquals("http://example.com/fabrikam/Purchasing", headers.getTo());
        assertEquals("http://example.com/fabrikam/SubmitPO", headers.getAction());
    }

    public void test200408Headers()
        throws Exception
    {
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder
                .build(getResourceAsStream("/org/codehaus/xfire/addressing/200408Headers1.xml"));

        AddressingHeadersFactory200408 factory = new AddressingHeadersFactory200408();

        assertTrue(factory.hasHeaders(doc.getRootElement()));

        AddressingHeaders headers = factory.createHeaders(doc.getRootElement());
        assertEquals("http://fabrikam123.example/mail/DeleteAck", headers.getAction());
        assertEquals("http://business456.example/client1", headers.getTo());
        assertEquals("uuid:aaaabbbb-cccc-dddd-eeee-wwwwwwwwwww", headers.getMessageID());
        assertEquals("uuid:aaaabbbb-cccc-dddd-eeee-ffffffffffff", headers.getRelatesTo());

        assertNotNull(headers.getReplyTo());
        EndpointReference ref = headers.getReplyTo();
        assertEquals("http://business456.example/client1", ref.getAddress());

        assertNotNull(ref.getReferenceParameters());
        assertEquals(1, ref.getReferenceParametersElement().getChildren().size());

        assertNotNull(ref.getReferenceProperties());
        assertEquals(1, ref.getReferenceProperties().size());

        Element header = new Element("Header", "s", Soap11.getInstance().getNamespace());
        doc = new Document(header);

        factory.writeHeaders(header, headers);

        addNamespace("wsa", WSAConstants.WSA_NAMESPACE_200408);
        assertValid("//wsa:Action[text()='" + headers.getAction() + "']", header);
        assertValid("//wsa:MessageID[text()='" + headers.getMessageID() + "']", header);
        assertValid("//wsa:ReplyTo/wsa:Address[text()='" + headers.getReplyTo().getAddress() + "']",
                    header);
    }
}
