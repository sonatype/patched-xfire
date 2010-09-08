package org.codehaus.xfire.attachments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.http.HttpTransport;
import org.codehaus.xfire.transport.http.SoapHttpTransport;
import org.jdom.Element;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;

public class ClientAttachmentTest
        extends AbstractXFireTest
{
    XFireHttpServer server;
    private Service service;
    
    public void setUp()
            throws Exception
    {
        super.setUp();

        service = getServiceFactory().create(AttachmentEcho.class);

        getServiceRegistry().register(service);
        
        server = new XFireHttpServer();
        server.setPort(8191);
        server.start();
    }

    protected XFire getXFire()
    {
        return XFireFactory.newInstance().getXFire();
    }

    protected void tearDown()
        throws Exception
    {
        getServiceRegistry().unregister(service);
        server.stop();
        
        super.tearDown();
    }


    public void testNonChunked() throws Exception
    {
        test(false);
    }
    
    public void testChunked() throws Exception
    {
        test(true);
    }
 
    public void test(boolean chunking)
            throws Exception
    {
        Client client = new Client(service.getBinding(SoapHttpTransport.SOAP11_HTTP_BINDING), 
                                   "http://localhost:8191/AttachmentEcho");

        client.setProperty(HttpTransport.CHUNKING_ENABLED, new Boolean(chunking).toString());
        
        File f = getTestFile("src/test/org/codehaus/xfire/attachments/echo11.xml");
        FileDataSource fs = new FileDataSource(f);

        final DataHandler dh = new DataHandler(fs);
        
        client.addOutHandler(new AbstractHandler() {

            public void invoke(MessageContext context)
                throws Exception
            {
                Attachments atts = new JavaMailAttachments();
                atts.addPart(new SimpleAttachment("test.jpg", dh));
                context.getOutMessage().setAttachments(atts);
            }
        });
        
        client.addInHandler(new AbstractHandler() {

            public void invoke(MessageContext context)
                throws Exception
            {
                Attachments atts = context.getInMessage().getAttachments();

                assertEquals(1, atts.size());
                Attachment att = atts.getPart("test.jpg");
                assertNotNull(att);
            }
        });

        client.invoke("echo", new Object[] { new Element("hi") });
    }

    public WebRequest getRequestMessage()
            throws Exception
    {
        JavaMailAttachments sendAtts = new JavaMailAttachments();

        sendAtts.setSoapMessage(new SimpleAttachment("echo.xml",
                                                     createDataHandler(
                                                             "src/test/org/codehaus/xfire/attachments/echo11.xml")));

        sendAtts.addPart(new SimpleAttachment("xfire_logo.jpg",
                                              createDataHandler(
                                                      "src/test/org/codehaus/xfire/attachments/xfire_logo.jpg")));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        sendAtts.write(bos);

        InputStream is = new ByteArrayInputStream(bos.toByteArray());

        PostMethodWebRequest req = new PostMethodWebRequest("http://localhost/services/AttachmentEcho",
                                                            is,
                                                            sendAtts.getContentType());

        return req;
    }

    private final DataHandler createDataHandler(String name)
            throws MessagingException
    {
        File f = getTestFile(name);
        FileDataSource fs = new FileDataSource(f);

        return new DataHandler(fs);
    }
}
