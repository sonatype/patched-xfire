package org.codehaus.xfire.attachments;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Manages attachments for an invocation.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @see org.codehaus.xfire.exchange.AbstractMessage
 */
public interface Attachments
{
    /**
     * @return Returns the SOAP Message.
     */
    Attachment getSoapMessage();

    /**
     * @param soapMessage The SOAP Message to set.
     */
    void setSoapMessage(Attachment soapMessage);

    void addPart(Attachment part);

    Iterator getParts();

    Attachment getPart(String id);

    int size();

    void write(OutputStream out) throws IOException;

    /**
     * Get the conetnt type of the whole message.
     * @return
     */
    String getContentType();

    /**
     * Get the content type of the soap message.
     * @return
     */
    String getSoapContentType();
    
    /**
     * Set the content type of the soap message.
     * @param soapMimeType
     */
    void setSoapContentType(String soapMimeType);
}