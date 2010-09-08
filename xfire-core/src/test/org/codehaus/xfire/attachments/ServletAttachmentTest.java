package org.codehaus.xfire.attachments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractServletTest;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;

/**
 * XFireServletTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ServletAttachmentTest
        extends AbstractServletTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        Service service = getServiceFactory().create(AttachmentEcho.class);

        getServiceRegistry().register(service);
    }

    public void testServlet()
            throws Exception
    {
        // Don't do anything because httpunit is b0rked
        WebRequest req = getRequestMessage();
        // WebResponse response = newClient().getResponse(req);

        // NOTE: At this point I would test that the response attachment
        // was sent successfully, but HttpUnit doesn't seem to preserve
        // the content type correctly :-(
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

    private DataHandler createDataHandler(String name)
            throws MessagingException
    {
        File f = getTestFile(name);
        FileDataSource fs = new FileDataSource(f);
        
        return new DataHandler(fs);
    }
}
