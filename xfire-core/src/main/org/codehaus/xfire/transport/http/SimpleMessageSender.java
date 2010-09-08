package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.STAXUtils;

/**
 * Sends a message via the JDK HTTP URLConnection. This is very buggy. Drop
 * commons-httpclient on your classpath and XFire will use CommonsHttpMessageSender instead.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class SimpleMessageSender extends AbstractMessageSender
{
    private HttpURLConnection urlConn;
    private InputStream is;

    public SimpleMessageSender(OutMessage message, MessageContext context)
    {
        super(message, context);
    }
    
    public void open() throws IOException, XFireFault
    {
        URL url = new URL(getUri());
        urlConn = createConnection(url);
        
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestMethod("POST");
        
        // Specify the content type.
        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        urlConn.setRequestProperty("User-Agent", "XFire Client +http://xfire.codehaus.org");
        urlConn.setRequestProperty("Accept", "text/xml; text/html");
        urlConn.setRequestProperty("Content-type", "text/xml; charset=" + getEncoding());

        urlConn.setRequestProperty( "SOAPAction", getQuotedSoapAction());
    }

    public OutputStream getOutputStream() throws IOException, XFireFault
    {
        return urlConn.getOutputStream();
    }
    
    public InMessage getInMessage() throws IOException
    {
        try
        {
            is = urlConn.getInputStream();
        }
        catch (IOException ioe)
        {
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
            {
                is = urlConn.getErrorStream();
            }
        }

        return new InMessage(STAXUtils.createXMLStreamReader(is, getEncoding(),getMessageContext()), getUri());
    }

    public void close() throws XFireException
    {
        
        try
        {
            if (is != null)
                is.close();
        }
        catch (IOException e)
        {
            throw new XFireException("Couldn't close stream.", e);
        }
        finally
        {
            if (urlConn != null)
                urlConn.disconnect();
        }
    }

    private HttpURLConnection createConnection(URL url)
        throws IOException
    {
        return (HttpURLConnection) url.openConnection();
    }

    public boolean hasResponse()
    {
        return true;
    }

    public void send()
        throws IOException, XFireFault
    {
        OutputStream out = getOutputStream();
        OutMessage message = getMessage();
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding(),null);

        message.getSerializer().writeMessage(message, writer, getMessageContext());
        
        out.flush();
        out.close();
    }


    public int getStatusCode()
    {
        return 0;
    }
}
