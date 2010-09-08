package org.codehaus.xfire.attachments;

import javax.activation.DataHandler;

/**
 * An attachment from a SOAP invocation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Attachment
{
    public DataHandler getDataHandler();

    /**
     * @return The attachment id.
     */
    public String getId();
    
    public String getHeader(String name);
    
    /**
     * Whether or not this is an XOP package. This will affect the 
     * serialization of the attachment. If true, it will be serialized
     * as binary data, and not Base64Binary.
     * 
     * @return
     */
    public boolean isXOP();
}