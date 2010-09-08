package org.codehaus.xfire.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.codehaus.xfire.XFireRuntimeException;

/**
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class JavaMailAttachments 
	implements Attachments
{
    private static final String[] filter = new String[]{"Message-ID", "Mime-Version", "Content-Type"};
    
    private Map parts;
    
    private Attachment soapMessage;

    private String soapContentType;
    
    private MimeMultipart mimeMP;
    
    public JavaMailAttachments()
    {
        parts = new HashMap();
    }
    
    public JavaMailAttachments(InputStream is, String contentType) 
        throws MessagingException, IOException
    {
        this();
        
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage inMsg = new MimeMessage(session, is);
        inMsg.addHeaderLine("Content-Type: " + contentType);

        final Object content = inMsg.getContent();

        if (content instanceof MimeMultipart)
        {
            MimeMultipart inMP = (MimeMultipart) content;

            initMultipart(inMP);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Create Attachments from the MimeMultipart message.
     * 
     * @param multipart
     * @throws MessagingException
     */
    public JavaMailAttachments(MimeMultipart multipart) 
    	throws MessagingException
    {
        this();
        
        initMultipart(multipart);   
    }
    
    private void initMultipart(MimeMultipart multipart) throws MessagingException
    {
        this.mimeMP = multipart;
        
        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(0);
        setSoapMessage(new SimpleAttachment(part.getContentID(), part.getDataHandler()));
        
        for ( int i = 1; i < multipart.getCount(); i++ )
        {
            part = (MimeBodyPart) multipart.getBodyPart(i);

            String id = part.getContentID();
            if (id.startsWith("<"))
            {
                id = id.substring(1, id.length() - 1);
            }
            
            addPart(new SimpleAttachment(id, part.getDataHandler()));
        }
    }
    
    /**
     * @return Returns the soapMessage.
     */
    public Attachment getSoapMessage()
    {
        return soapMessage;
    }
    
    /**
     * @param soapMessage The soapMessage to set.
     */
    public void setSoapMessage(Attachment soapMessage)
    {
        this.soapMessage = soapMessage;
    }
    
    public void addPart(Attachment part)
    {
        parts.put(part.getId(), part);
    }
    
    public Iterator getParts()
    {
        return parts.values().iterator();
    }
    
    public Attachment getPart(String id)
    {
        return (Attachment) parts.get(id);
    }

    public int size()
    {
        return parts.size();
    }
    
    public void write(OutputStream out) 
    	throws IOException
    {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage message = new MimeMessage(session);
        
        try
        {
            MimeMultipart mimeMP = getMimeMultipart();

            MimeBodyPart soapPart = new MimeBodyPart();
            soapPart.setDataHandler(soapMessage.getDataHandler());
            soapPart.setContentID("<"+soapMessage.getId()+">");
            soapPart.addHeader("Content-Transfer-Encoding", "8bit");
            mimeMP.addBodyPart(soapPart);
            
            for (Iterator itr = getParts(); itr.hasNext(); )
            {
                Attachment att = (Attachment) itr.next();
                
                MimeBodyPart part = new MimeBodyPart();
                part.setDataHandler(att.getDataHandler());
                part.setContentID("<"+att.getId()+">");

                if (att.isXOP())
                    part.addHeader("Content-Transfer-Encoding", "binary");

                mimeMP.addBodyPart(part);
            }
            
            message.setContent(mimeMP);
	        message.writeTo(out, filter);
        }
        catch( MessagingException e )
        {
            throw new XFireRuntimeException("Couldn't create message.", e);
        }
    }
    
    public MimeMultipart getMimeMultipart()
    {
        if ( mimeMP == null )
        {
            StringBuffer ct = new StringBuffer();
            ct.append("related; type=\"")
              .append("application/xop+xml")
              .append("\"; start=\"<")
              .append(getSoapMessage().getId())
              .append(">\"; start-info=\"")
              .append(getSoapContentType())
              .append("\"");

            mimeMP = new MimeMultipart(ct.toString());
        }

        return mimeMP;
    }
    
    public String getContentType()
    {
        return getMimeMultipart().getContentType();
    }

    /**
     * The Content-Type of the SOAP message part.
     * @return
     */
    public String getSoapContentType()
    {
        return soapContentType;
    }

    public void setSoapContentType(String soapContentType)
    {
        this.soapContentType = soapContentType;
    }

    public void dispose()
    {
    }   
}
