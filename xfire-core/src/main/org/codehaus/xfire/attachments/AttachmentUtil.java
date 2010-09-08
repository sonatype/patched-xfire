package org.codehaus.xfire.attachments;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.util.UID;

public class AttachmentUtil
{
    public static String createContentID(String ns)
    {
        String uid = UID.generate();
        try
        {
            URI uri = new URI(ns);
            return uid + "@" + uri;
        }
        catch (URISyntaxException e)
        {
            throw new XFireRuntimeException("Could not create URI for namespace: " + ns);
        }
    }
    
    public static Attachment getAttachment(String id, AbstractMessage msg) 
    {
        int i = id.indexOf("cid:");
        if (i != -1) id = id.substring(4).trim();
        
        Attachments atts = msg.getAttachments();
        if (atts == null)
            return null;
        
        Attachment att = atts.getPart(id);

        // Try loading the URL remotely
        if (att == null)
        {
            try
            {
                URLDataSource source = new URLDataSource(new URL(id));
                att = new SimpleAttachment(id, new DataHandler(source));
            }
            catch (MalformedURLException e)
            {
                return null;
            }
        }
        
        return att;
    }
}
