package org.codehaus.xfire.addressing;

import java.lang.reflect.Method;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.TestWSAServiceImpl;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSAddressingNotify1_2Test
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

                new AddressingOperationInfo("http://example.org/action/notify", op);

                return op;
            }

        };
        factory.setStyle("document");
        service = factory.create(TestWSAServiceImpl.class);

        service.addInHandler(new WSATestHandler(data));
        if (getXFire().getInHandlers().size() < 3)
        {
            ((DefaultXFire) getXFire()).addInHandler(new AddressingInHandler());
            ((DefaultXFire) getXFire()).addFaultHandler(new AddressingOutHandler());
        }

        getServiceRegistry().register(service);
    }

    public void test1200()
        throws Exception
    {
        // /soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/notify

        invokeService(SERVICE_NAME,
                      "/org/codehaus/xfire/addressing/testcases/notify/soap12/message0.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/notify");

    }

    public void test1201()
        throws Exception
    {
        // soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/notify
        // soap12:Envelope/soap12:Header/wsa:MessageID{regex}.*

        invokeService(SERVICE_NAME,
                      "/org/codehaus/xfire/addressing/testcases/notify/soap12/message1.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/notify");
        assertNotNull(data.getInHeaders().getMessageID());

    }

    /**
     * @throws Exception
     */
    public void test1202()
        throws Exception
    {
        // soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/notify
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/none

        invokeService(SERVICE_NAME,
                      "/org/codehaus/xfire/addressing/testcases/notify/soap12/message2.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/notify");
        assertEquals(data.getInHeaders().getReplyTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/none");

    }

    /**
     * @throws Exception
     */
    public void test1203()
        throws Exception
    {
        // soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/notify
        // soap12:Envelope/soap12:Header/wsa:FaultTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/none

        invokeService(SERVICE_NAME,
                      "/org/codehaus/xfire/addressing/testcases/notify/soap12/message3.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/notify");
        assertEquals(data.getInHeaders().getFaultTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/none");

    }

    /**
     * @throws Exception
     */
    public void test1204()
        throws Exception
    {
        // soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/notify
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/none
        // soap12:Envelope/soap12:Header/wsa:FaultTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/none

        invokeService(SERVICE_NAME,
                      "/org/codehaus/xfire/addressing/testcases/notify/soap12/message4.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/notify");
        assertEquals(data.getInHeaders().getReplyTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/none");
        assertEquals(data.getInHeaders().getFaultTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/none");

    }

    /*
     * public void test1205() throws Exception { //A sends a message to B.
     * //soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/notify
     * //soap12:Envelope/soap12:Header/wsa:FaultTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/none
     * //soap12:Envelope/soap12:Header/alertcontrol:alertcontrol/@soap12:mustUnderstand{bool}true //
     * B sends a fault to A.
     * //soap12:Envelope/soap12:Header/soap12:NotUnderstood/@qname{qname}alertcontrol:alertcontrol
     * //soap12:Envelope/soap12:Body/soap12:Fault/soap12:Code/soap12:Value{qname}soap12:MustUnderstand
     * Document response = null; try{ response =
     * invokeService("TestWSAServiceImpl",
     * 
     * "/org/codehaus/xfire/addressing/testcases/notify/soap12/message5.xml");
     * }catch(Exception ex){ int z=0; } //DOMOutputter writer = new
     * DOMOutputter(); //XMLOutputter outp = new XMLOutputter();
     * //outp.output(response,System.out);
     * assertEquals(data.getInHeaders().getAction(),
     * "http://example.org/action/notify");
     * assertEquals(data.getInHeaders().getFaultTo().getAddress(),
     * "http://www.w3.org/2005/08/addressing/none"); }
     */
    public void test1206()
        throws Exception
    {
        // soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/notify
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/none
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:ReferenceParameters/customer:CustomerKey{match}Key#123456789

        invokeService(SERVICE_NAME,

        "/org/codehaus/xfire/addressing/testcases/notify/soap12/message7.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/notify");
        assertEquals(data.getInHeaders().getReplyTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/none");
        Element params = data.getInHeaders().getReplyTo().getReferenceParametersElement();
        assertNotNull(params);
        Element customerKey = params.getChild("CustomerKey", Namespace
                .getNamespace("customer", "http://example.org/customer"));
        assertNotNull(customerKey);
        assertEquals(customerKey.getValue(), "Key#123456789");

    }

    public void test1207()
        throws Exception
    {
        // soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/notify
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/none
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:ReferenceParameters/customer:CustomerKey{match}Key#123456789
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Metadata/wsdl11:definitions{exists}
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Metadata/wsdl20:definitions{exists}

        invokeService(SERVICE_NAME,

        "/org/codehaus/xfire/addressing/testcases/notify/soap12/message8.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/notify");
        assertEquals(data.getInHeaders().getReplyTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/none");
        Element params = data.getInHeaders().getReplyTo().getReferenceParametersElement();

        assertNotNull(params);

        Element customerKey = params.getChild("CustomerKey", Namespace
                .getNamespace("customer", "http://example.org/customer"));
        assertNotNull(customerKey);
        assertEquals(customerKey.getValue(), "Key#123456789");

        Element metadata = data.getInHeaders().getReplyTo().getMetadataElement();
        assertNotNull(metadata);

        Element wsdl11 = metadata.getChild("definitions", Namespace
                .getNamespace("http://schemas.xmlsoap.org/wsdl/"));
        assertNotNull(wsdl11);

        Element wsdl12 = metadata.getChild("definitions", Namespace
                .getNamespace("http://www.w3.org/2005/08/wsdl"));
        assertNotNull(wsdl12);

    }

    public void test1208()
        throws Exception
    {
        // soap12:Envelope/soap12:Header/wsa:Action{match}http://example.org/action/notify
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Address{match}http://www.w3.org/2005/08/addressing/none
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:ReferenceParameters/@customer:level{match}premium
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:ReferenceParameters/customer:CustomerKey{match}Key#123456789
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/wsa:Metadata/@customer:total{match}1
        // soap12:Envelope/soap12:Header/wsa:ReplyTo/customer:Metadata{exists}

        invokeService(SERVICE_NAME,

        "/org/codehaus/xfire/addressing/testcases/notify/soap12/message9.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/notify");
        assertEquals(data.getInHeaders().getReplyTo().getAddress(),
                     "http://www.w3.org/2005/08/addressing/none");
        Element params = data.getInHeaders().getReplyTo().getReferenceParametersElement();
        final Namespace customerNamespace = Namespace.getNamespace("customer",
                                                                   "http://example.org/customer");
        Attribute level = params.getAttribute("level", customerNamespace);
        assertNotNull(level);
        assertEquals(level.getValue(), "premium");
        Element customerKey = params.getChild("CustomerKey", customerNamespace);
        assertNotNull(customerKey);
        assertEquals(customerKey.getValue(), "Key#123456789");

        Element metadata = data.getInHeaders().getReplyTo().getMetadataElement();
        assertNotNull(metadata);
        Attribute total = metadata.getAttribute("total", Namespace
                .getNamespace("customer", "http://example.org/customer"));
        assertNotNull(total);
        assertEquals(total.getValue(), "1");
        Element meta = data.getInHeaders().getReplyTo()
                .getChild("Metadata",
                          Namespace.getNamespace("customer", "http://example.org/customer"));
        assertNotNull(meta);

    }

}
