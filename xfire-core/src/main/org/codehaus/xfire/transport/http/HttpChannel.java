package org.codehaus.xfire.transport.http;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.AbstractChannel;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.util.STAXUtils;

public class HttpChannel
    extends AbstractChannel
{
    private static final Log log = LogFactory.getLog(HttpChannel.class);
    
    public static final String HTTP_STATUS_CODE = "http.status.code";
    
    private Map properties = new HashMap();
    
    public HttpChannel(String uri, HttpTransport transport)
    {
        setTransport(transport);
        setUri(uri);
    }

    public void open()
    {
    }

    public void send(MessageContext context, OutMessage message) throws XFireException
    {
        sendViaClient(context, message);
    }

    public static void writeWithoutAttachments(MessageContext context, OutMessage message, OutputStream out) 
        throws XFireException
    {
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(out, message.getEncoding(), context);
        
        message.getSerializer().writeMessage(message, writer, context);
        
        try
        {
            writer.flush();
        }
        catch (XMLStreamException e)
        {
            log.error(e);
            throw new XFireException("Couldn't send message.", e);
        }
    }

    public static String getSoapMimeType(AbstractMessage msg, boolean includeEncoding)
    {
        StringBuffer ct = new StringBuffer();
        SoapVersion soap = msg.getSoapVersion();
        if (soap instanceof Soap11)
        {
            ct.append("text/xml");
        }
        else if (soap instanceof Soap12)
        {
             ct.append("application/soap+xml");
        }
        else
        {
            ct.append("text/xml");
        }
        
        if (includeEncoding)
        {
            ct.append("; charset=")
              .append(msg.getEncoding());
        }
        
        return ct.toString();
    }

    protected void sendViaClient(MessageContext context, OutMessage message)
        throws XFireException
    {
        AbstractMessageSender sender;

        String clazz = (String) context.getContextualProperty(AbstractMessageSender.MESSAGE_SENDER_CLASS_NAME);
        if (clazz == null) {
            clazz = "org.codehaus.xfire.transport.http.CommonsHttpMessageSender";
        }

        try
        {
            Class chms = ClassLoaderUtils.loadClass(clazz, getClass());
            Constructor constructor = chms.getConstructor(new Class[] {OutMessage.class, MessageContext.class});
            sender = (AbstractMessageSender) constructor.newInstance(new Object[] { message, context });
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.debug("Could not load message sender class " + clazz + ". Using buggy SimpleMessageSender instead.");

            sender = new SimpleMessageSender(message, context);
        }
        
        try
        {
            sender.open();
            
            sender.send();
            int statusCode = sender.getStatusCode();
            context.setProperty(HTTP_STATUS_CODE, new Integer(statusCode));
            // http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
            if( statusCode !=500 && ( ( statusCode >= 400 &&  statusCode < 600 ) || statusCode == 301 )){
                String msg = "Server returned error code = " + statusCode + " for URI : "+ sender.getUri() + ". Check server logs for details";
                log.error(msg); 
                throw new XFireRuntimeException(msg);
            }
            
            
            if (sender.hasResponse())
            {   
                
                InMessage inMessage = sender.getInMessage();
                inMessage.setChannel(this);
                getEndpoint().onReceive(context, inMessage);
            }
        }
        catch (IOException e)
        {
            log.error(e);
            throw new XFireException("Couldn't send message.", e);
        }
        finally
        {
            sender.close();
        }
    }

    public void close()
    {
        properties.clear();
        
        super.close();
    }

    public boolean isAsync()
    {
        return false;
    }
    
    public Object getProperty(String key)
    {
        return properties.get(key);
    }
    
    public void setProperty(String key, Object value)
    {
        properties.put(key, value);
    }
}
