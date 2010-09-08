package org.codehaus.xfire.addressing;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.codehaus.xfire.DefaultXFire;
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
public class WSAddresingEcho1_1Test
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
        //service = factory.create(EchoImpl.class);
        service.addInHandler(new WSATestHandler(data));
        if (getXFire().getInHandlers().size() < 3)
        {
            ((DefaultXFire) getXFire()).addInHandler(new AddressingInHandler());
            ((DefaultXFire) getXFire()).addOutHandler(new AddressingOutHandler());
            ((DefaultXFire) getXFire()).addFaultHandler(new AddressingOutHandler());
        }

        getServiceRegistry().register(service);
    }

    public void test1131()
        throws Exception
    {

        //A sends a message to B. 
        //soap11:Envelope/soap11:Header/wsa:Action = 'http://example.org/action/echoIn'
        //soap11:Envelope/soap11:Header/wsa:ReplyTo/wsa:Address = 'http://www.w3.org/2005/08/addressing/anonymous'
        //soap11:Envelope/soap11:Header/wsa:MessageID
        //B sends a reply to A. 
        //soap11:Envelope/soap11:Header/wsa:Action = 'http://example.org/action/echoOut'
        
        invokeService(SERVICE_NAME,
                                     "/org/codehaus/xfire/addressing/testcases/echo/soap11/message1.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/echoIn");
        assertEquals(data.getInHeaders().getReplyTo().getAddress(),"http://www.w3.org/2005/08/addressing/anonymous");
        assertNotNull(data.getInHeaders().getMessageID());
        assertEquals(data.getOutHeaders().getAction(), "http://example.org/action/echoOut");

       
    }

    
    public void test1133()
    throws Exception
{
    //A sends a message to B. 
    //soap11:Envelope/soap11:Header/wsa:Action = 'http://example.org/action/echoIn'
     //   soap11:Envelope/soap11:Header/wsa:ReplyTo/wsa:Address = 'http://www.w3.org/2005/08/addressing/anonymous'
      //  soap11:Envelope/soap11:Header/wsa:ReplyTo/wsa:ReferenceParameters/customer:CustomerKey = 'Key#123456789'
      //  soap11:Envelope/soap11:Header/wsa:FaultTo/wsa:ReferenceParameters/customer:CustomerKey = 'Fault#123456789'
      //  soap11:Envelope/soap11:Header/wsa:FaultTo/wsa:Address = 'http://www.w3.org/2005/08/addressing/anonymous'
      //  B sends a fault to A. 
      //  soap11:Envelope/soap11:Header/customer:CustomerKey = 'Fault#123456789'
      //  soap11:Envelope/soap11:Header/customer:CustomerKey/@wsa:isReferenceParameter cast as xs:boolean = true()
      //  soap11:Envelope/soap11:Body/soap11:Fault/soap11:faultcode/(resolve-QName(.,.) = xs:QName('echo:EmptyEchoString'))
    Document doc = invokeService(SERVICE_NAME,
                                 "/org/codehaus/xfire/addressing/testcases/echo/soap11/message8.xml");

    XMLOutputter output = new XMLOutputter();
    output.output(doc, System.out);
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
    assertValid("/soap:Envelope/soap:Header/customer:CustomerKey[text()='Fault#123456789']",
                doc);
    assertValid("/soap:Envelope/soap:Header/customer:CustomerKey[@wsa:isReferenceParameter='true']",
                doc);
  
    assertValid("/soap:Envelope/soap:Body/soap:Fault/faultcode[text()='echo:EmptyEchoString']",
                doc);

}
    public void test1150()
        throws Exception
    {

        // A sends a message to B.
        // soap11:Envelope/soap11:Header/wsa:Action =
        // 'http://example.org/action/echoIn'
        // soap11:Envelope/soap11:Header/wsa:ReplyTo/wsa:Address
        // not(soap11:Envelope/soap11:Header/wsa:ReplyTo/wsa:Address = '')
        // not(soap11:Envelope/soap11:Header/wsa:ReplyTo/wsa:Address =
        // 'http://www.w3.org/2005/08/addressing/anonymous')
        // B sends a reply to A.
        // soap11:Envelope/soap11:Header/wsa:Action =
        // 'http://example.org/action/echoOut'

       /* Document doc = invokeService(SERVICE_NAME,
                                     "/org/codehaus/xfire/addressing/testcases/echo/soap11/test1150request.xml");*/

        // assertValid()

    }

}
