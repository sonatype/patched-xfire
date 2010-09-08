package org.codehaus.xfire.addressing;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.addressing.AddressingInHandler;
import org.codehaus.xfire.addressing.AddressingOperationInfo;
import org.codehaus.xfire.addressing.AddressingOutHandler;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.TestWSAServiceImpl;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSAddresingEcho1_2Test
    extends AbstractXFireTest
{

    private static final String SERVICE_NAME = "TestWSAServiceImpl";

    private AddressingInData data = null;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        data = new AddressingInData();
        Service service;
        ObjectServiceFactory factory = new ObjectServiceFactory(getXFire().getTransportManager(),
                new MessageBindingProvider())
        {

            protected OperationInfo addOperation(Service endpoint, Method method, String use)
            {
                OperationInfo op = super.addOperation(endpoint, method, use);

                new AddressingOperationInfo("http://example.org/action/echoIn",
                        "http://example.org/action/echoOut", op);
                return op;
            }

            protected QName getInParameterName(Service endpoint,
                                               OperationInfo op,
                                               Method method,
                                               int paramNumber,
                                               boolean doc)
            {
                return new QName("http://example.org/echo", "echo");
            }
        };
        factory.setStyle("document");
        service = factory.create(TestWSAServiceImpl.class);
        service.addInHandler(new WSATestHandler(data));
        if (getXFire().getInHandlers().size() < 3)
        {
            ((DefaultXFire) getXFire()).addInHandler(new AddressingInHandler());
            ((DefaultXFire) getXFire()).addOutHandler(new AddressingOutHandler());
            ((DefaultXFire) getXFire()).addFaultHandler(new AddressingOutHandler());
        }

        getServiceRegistry().register(service);
    }

    public void test1230()
        throws Exception
    {
        // A sends a message to B.
        // /soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/echoIn
        // B sends a reply to A.
        // /soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/echoOut

        invokeService(SERVICE_NAME,
                                     "/org/codehaus/xfire/addressing/testcases/echo/soap12/message1.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/echoIn");
        assertEquals(data.getOutHeaders().getAction(), "http://example.org/action/echoOut");

    }

    public void test1231()
        throws Exception
    {
        // A sends a message to B.
        // /soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/echoIn
        // /soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address
        // ='http://www.w3.org/2005/08/addressing/anonymous'
        // /soap12:Envelope/soap12:Header/wsa:MessageID
        // B sends a reply to A.
        // /soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/echoOut

        invokeService(SERVICE_NAME,
                      "/org/codehaus/xfire/addressing/testcases/echo/soap12/message1.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/echoIn");
        assertEquals(data.getInHeaders().getReplyTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/anonymous");
        assertNotNull(data.getInHeaders().getMessageID());
        assertEquals(data.getOutHeaders().getAction(), "http://example.org/action/echoOut");

    }

    public void test1232()
        throws Exception
    {
        // A sends a message to B.
        // /soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/echoIn
        // /soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/anonymous
        // /soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:ReferenceParameters/customer:CustomerKey{match}Key#123456789
        // B sends a reply to A.
        // /soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/echoOut
        // /soap12:Envelope/soap12:Header/customer:CustomerKey{match}Key#123456789
        // /soap12:Envelope/soap12:Header/customer:CustomerKey/@wsa:isReferenceParameter{bool}true

        Document doc = invokeService(SERVICE_NAME,
                                     "/org/codehaus/xfire/addressing/testcases/echo/soap12/message7.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/echoIn");
        assertEquals(data.getInHeaders().getReplyTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/anonymous");
        Element refParams = data.getInHeaders().getReplyTo().getReferenceParametersElement();
        Element customerKey = refParams.getChild("CustomerKey", Namespace
                .getNamespace("customer", "http://example.org/customer"));
        assertNotNull(customerKey);
        assertEquals(customerKey.getValue(), "Key#123456789");

        assertEquals(data.getOutHeaders().getAction(), "http://example.org/action/echoOut");

        addNamespace("customer", "http://example.org/customer");
        addNamespace("wsa", "http://www.w3.org/2005/08/addressing");
        assertValid("/soap:Envelope/soap:Header/customer:CustomerKey[text()='Key#123456789']", doc);
        assertValid("/soap:Envelope/soap:Header/customer:CustomerKey[@wsa:isReferenceParameter='true']",
                    doc);
    }

    public void test1233()
        throws Exception
    {
        // A sends a message to B.
        // /soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/echoIn
        // /soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/anonymous
        // /soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:ReferenceParameters/customer:CustomerKey{match}Key#123456789
        // /soap12:Envelope/soap12:Header/wsa:FaultTo/wsa:ReferenceParameters/customer:CustomerKey{match}Fault#123456789
        // /soap12:Envelope/soap12:Header/wsa:FaultTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/anonymous
        // B sends a fault to A.
        // /soap12:Envelope/soap12:Header/wsa:Action =
        // 'http://www.w3.org/2005/08/addressing/fault'
        // /soap12:Envelope/soap12:Header/customer:CustomerKey{match}Fault#123456789
        // /soap12:Envelope/soap12:Header/customer:CustomerKey/@wsa:isReferenceParameter{bool}true
        // /soap12:Envelope/soap12:Body/soap12:Fault/soap12:Code/soap12:Value{qname}echo:EmptyEchoString

        Document doc = invokeService(SERVICE_NAME,
                                     "/org/codehaus/xfire/addressing/testcases/echo/soap12/message8.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/echoIn");
        assertEquals(data.getInHeaders().getReplyTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/anonymous");
        Element refParams = data.getInHeaders().getReplyTo().getReferenceParametersElement();
        Element customerKey = refParams.getChild("CustomerKey", Namespace
                .getNamespace("customer", "http://example.org/customer"));
        assertEquals(customerKey.getValue(), "Key#123456789");

        assertEquals(data.getInHeaders().getFaultTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/anonymous");
        refParams = data.getInHeaders().getFaultTo().getReferenceParametersElement();
        customerKey = refParams.getChild("CustomerKey", Namespace
                .getNamespace("customer", "http://example.org/customer"));
        assertEquals(customerKey.getValue(), "Fault#123456789");

        addNamespace("customer", "http://example.org/customer");
        addNamespace("wsa", "http://www.w3.org/2005/08/addressing");
        assertValid("/soap:Envelope/soap:Header/wsa:Action[text()='http://www.w3.org/2005/08/addressing/fault']",
                    doc);

        assertValid("/soap:Envelope/soap:Header/customer:CustomerKey[text()='Fault#123456789']",
                    doc);
        assertValid("/soap:Envelope/soap:Header/customer:CustomerKey[@wsa:isReferenceParameter='true']",
                    doc);

        assertValid("/soap:Envelope/soap:Body/soap:Fault/soap:Code/soap:Value[text()='echo:EmptyEchoString']",
                    doc);

    }

    public void test1234()
        throws Exception
    {
        // A sends a message to B.
        // /soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/echoIn
        // /soap12:Envelope/soap12:Header/wsa:To{match}http://www.w3.org/2005/08/addressing/anonymous
        // /soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:ReferenceParameters/customer:CustomerKey{match}Key#123456789
        // B sends a fault to A.
        // /soap12:Envelope/soap12:Header/wsa:Action = 'http://www.w3.org/2005/08/addressing/fault'
        // /soap12:Envelope/soap12:Header/customer:CustomerKey{match}Fault#123456789
        // /soap12:Envelope/soap12:Header/customer:CustomerKey/@wsa:isReferenceParameter{bool}true
        // /soap12:Envelope/soap12:Body/soap12:Fault/soap12:Code/soap12:Value{qname}echo:EmptyEchoString

        Document doc = invokeService(SERVICE_NAME,
                                     "/org/codehaus/xfire/addressing/testcases/echo/soap12/message8.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/echoIn");
        assertEquals(data.getInHeaders().getTo(), "http://www.w3.org/2005/08/addressing/anonymous");
        Element refParams = data.getInHeaders().getReplyTo().getReferenceParametersElement();
        Element customerKey = refParams.getChild("CustomerKey", Namespace
                .getNamespace("customer", "http://example.org/customer"));
        assertEquals(customerKey.getValue(), "Key#123456789");

        addNamespace("customer", "http://example.org/customer");
        addNamespace("wsa", "http://www.w3.org/2005/08/addressing");
        assertValid("/soap:Envelope/soap:Header/wsa:Action[text()='http://www.w3.org/2005/08/addressing/fault']",
                    doc);
        assertValid("/soap:Envelope/soap:Header/customer:CustomerKey[text()='Fault#123456789']",
                    doc);
        assertValid("/soap:Envelope/soap:Header/customer:CustomerKey[@wsa:isReferenceParameter='true']",
                    doc);

        assertValid("/soap:Envelope/soap:Body/soap:Fault/soap:Code/soap:Value[text()='echo:EmptyEchoString']",
                    doc);

    }

    public void test1240()
        throws Exception
    {
        // A sends a message to B.
        
        // B sends a fault to A.
        // soap12:Envelope/soap12:Header/wsa:Action{match}http://www.w3.org/2005/08/addressing/fault
        // soap12:Envelope/soap12:Body/wsa:Fault/soap12:Code/soap12:Value{qname}soap12:Sender
        // soap12:Envelope/soap12:Body/wsa:Fault/soap12:Code/soap12:SubCode{qname}wsa:InvalidAddressingHeader
        // (OPTIONAL)
        // soap12:Envelope/soap12:Body/wsa:Fault/soap12:Reason/soap12:Detail/wsa:ProblemHeader{qname}wsa:ReplyTo
        // (OPTIONAL)
        Document doc = invokeService(SERVICE_NAME,
                                     "/org/codehaus/xfire/addressing/testcases/echo/soap12/duplicateFaultToRequest.xml");

        
        XMLOutputter output = new XMLOutputter(); 
        output.output(doc, System.out);
        addNamespace("wsa", "http://www.w3.org/2005/08/addressing");
        assertValid("/soap:Envelope/soap:Header/wsa:Action[text()='http://www.w3.org/2005/08/addressing/fault']",
                    doc);
        assertValid("/soap:Envelope/soap:Body/soap:Fault/soap:Code/soap:Value[text()='ns1:Sender']",
                    doc);
        assertValid("/soap:Envelope/soap:Body/soap:Fault/soap:Code/soap:SubCode/soap:Value[text()='ns1:InvalidAddressingHeader']",
                    doc);
        
        // TODO : implement optional part ( ProblemHeader )
    }

    public void test1250()
        throws Exception
    {

        // Two-way message exchange containing an Action and a ReplyTo
        // identifying an endpoint. All other fields are defaulted.

        // SOAP12-HTTP-In-Out-Callback
        // A sends a message to B.
        // soap12:Envelope/soap12:Header/wsa:Action =
        // 'http://example.org/action/echoIn'
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address
        // not(soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address = '')
        // not(soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address =
        // 'http://www.w3.org/2005/08/addressing/anonymous')
        // B sends a reply to A.
        // soap12:Envelope/soap12:Header/wsa:Action =
        // 'http://example.org/action/echoOut'
        /*
         * Document doc = invokeService(SERVICE_NAME,
         * "/org/codehaus/xfire/addressing/testcases/echo/soap12/test1250request.xml");
         * 
         * XMLOutputter output = new XMLOutputter(); output.output(doc,
         * System.out);
         * assertEquals(data.getInHeaders().getAction(),"http://example.org/action/echoIn");
         * assertTrue(data.getInHeaders().getReplyTo().getAddress() != null);
         * assertTrue(data.getInHeaders().getReplyTo().getAddress().length() >
         * 0);
         * assertTrue(!data.getInHeaders().getReplyTo().getAddress().equals("http://www.w3.org/2005/08/addressing/anonymous"));
         * 
         * assertEquals(data.getOutHeaders().getAction(),
         * "http://example.org/action/echoOut");
         */
    }
}
