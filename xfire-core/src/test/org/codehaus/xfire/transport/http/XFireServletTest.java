package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.service.AsyncService;
import org.codehaus.xfire.service.BadEcho;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractServletTest;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletUnitClient;

/**
 * XFireServletTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireServletTest
        extends AbstractServletTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        osf.addSoap12Transport(SoapHttpTransport.SOAP12_HTTP_BINDING);
        Service service = osf.create(EchoImpl.class);
        WSDLWriter writer = new ResourceWSDL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl"));
        service.setWSDLWriter(writer);

        service.addInHandler(new MockSessionHandler());
        getServiceRegistry().register(service);

        Service faultService = getServiceFactory().create(BadEcho.class);

        getServiceRegistry().register(faultService);
        
        // Asynchronous service
        Service asyncService = osf.create(AsyncService.class);
        OperationInfo op = asyncService.getServiceInfo().getOperation("echo");
        op.setMEP(SoapConstants.MEP_IN);
        op.setOutputMessage(null);
        getServiceRegistry().register(asyncService);
    }

    public void testServlet()
            throws Exception
    {
    	WebRequest getReq = new GetMethodWebRequest("http://localhost/services/EchoImpl?wsdl")
        {

            /*
             * Work around bug 1212204 in httpUnit where as of 1.6 there was not
             * a way to support query strings with null values.
             * 
             * @see com.meterware.httpunit.HeaderOnlyWebRequest#getQueryString()
             */
            public String getQueryString()
            {
                return "WSDL";
            }
        };
        
    	WebResponse response = newClient().getResponse(getReq);

        WebRequest req = new PostMethodWebRequest("http://localhost/services/EchoImpl",
                                                  getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                                                  "text/xml");

        response = newClient().getResponse(req);

        assertEquals("text/xml", response.getContentType());
        assertEquals("UTF-8", response.getCharacterSet());
        
        Document doc = readDocument(response.getText());
        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", doc);

        assertTrue(MockSessionHandler.inSession);
    }

    public void testServlet12()
            throws Exception
    {
        WebRequest req = new PostMethodWebRequest("http://localhost/services/EchoImpl",
                                                  getClass().getResourceAsStream("/org/codehaus/xfire/echo12.xml"),
                                                  "text/xml");

        WebResponse response = newClient().getResponse(req);

        assertEquals("application/soap+xml", response.getContentType());
        assertEquals("UTF-8", response.getCharacterSet());
        
        Document doc = readDocument(response.getText());
        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", doc);

        assertTrue(MockSessionHandler.inSession);
    }
    
    public void testFaultCode()
            throws Exception
    {
        WebRequest req = new PostMethodWebRequest("http://localhost/services/BadEcho",
                                                  getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                                                  "text/xml");

        Transport transport = getXFire().getTransportManager().getTransport(SoapHttpTransport.SOAP11_HTTP_BINDING);
        assertNotNull(transport.getFaultHandlers());

        expectErrorCode(req, 500, "Response code 500 required for faults.");
    }
    
    public void testInvalidServiceUrl()
        throws Exception
    {
        ServletUnitClient client = newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        
        WebResponse res = client.getResponse("http://localhost/services/NoSuchService");
        assertEquals(404, res.getResponseCode());
        assertTrue(res.isHTML());
    }
    
    public void testServiceUrlNoSOAPMessage()
        throws Exception
    {
        ServletUnitClient client = newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        
        WebResponse res = client.getResponse("http://localhost/services/EchoImpl");

        assertTrue(res.isHTML());
        assertEquals("<html><body>Invalid SOAP request.</body></html>", res.getText());
    }
     
    public void testServiceWsdlNotFound()
            throws Exception
    {
        WebRequest req = new GetMethodWebRequest("http://localhost/services/NoSuchService?wsdl");

        expectErrorCode(req, 404, "Response code 404 required for invalid WSDL url.");
    }

    public void testAsync()
            throws Exception
    {
        WebRequest req = new PostMethodWebRequest("http://localhost/services/AsyncService",
                                                  getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                                                  "text/xml");

        WebResponse response = newClient().getResponse(req);
        assertTrue(response.getText().length() == 0);
    }
    // 800 423 4343
}
