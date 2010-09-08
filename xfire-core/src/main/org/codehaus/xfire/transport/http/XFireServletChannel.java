package org.codehaus.xfire.transport.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.attachments.JavaMailAttachments;
import org.codehaus.xfire.attachments.SimpleAttachment;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.OutMessageDataSource;

/**
 * @author Dan Diephouse
 */
public class XFireServletChannel extends HttpChannel
{
    
    public XFireServletChannel(String uri, HttpTransport transport)
    {
        super(uri, transport);
    }

    public void send(MessageContext context, OutMessage message) throws XFireException
    {
        if (message.getUri().equals(Channel.BACKCHANNEL_URI))
        {
            HttpServletResponse response = XFireServletController.getResponse();
            
            if (response == null)
            {
                throw new XFireRuntimeException("No backchannel exists for message");
            }
            
            sendViaServlet(context, message, response);
        }
        else
        {
            sendViaClient(context, message);
        }
    }
    
    protected void sendViaServlet(MessageContext context, OutMessage message, HttpServletResponse response)
        throws XFireException
    {
        try
        {
            OutputStream out = null;
            
            boolean mtomEnabled = Boolean.valueOf((String) context.getContextualProperty(SoapConstants.MTOM_ENABLED)).booleanValue();
            Attachments atts = message.getAttachments();
            if (mtomEnabled || atts != null)
            {
                if (atts == null)
                {
                    atts = new JavaMailAttachments();
                    message.setAttachments(atts);
                }
                
                OutMessageDataSource source = new OutMessageDataSource(context, message);
                DataHandler soapHandler = new DataHandler(source);
                atts.setSoapContentType(HttpChannel.getSoapMimeType(message, false));
                atts.setSoapMessage(new SimpleAttachment(source.getName(), soapHandler));
    
                response.setContentType(atts.getContentType());
                
                out = new BufferedOutputStream(response.getOutputStream());
                atts.write(out);
                
                source.dispose();
            }
            else
            {
                response.setContentType(doGetSoapMimeType(message, true));
                
                out = new BufferedOutputStream(response.getOutputStream());
                message.setProperty(Channel.OUTPUTSTREAM, out);
                HttpChannel.writeWithoutAttachments(context, message, out);
            }
            
            out.close();
        }
        catch (IOException e)
        {
            throw new XFireException("Couldn't send message.", e);
        }
    }

    protected String doGetSoapMimeType(AbstractMessage msg, boolean includeEncoding)
    {
        return HttpChannel.getSoapMimeType(msg, includeEncoding);
    }
}
