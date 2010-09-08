package org.codehaus.xfire.attachments;

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SimpleAttachment
	implements Attachment
{
    private DataHandler handler;
    private	String id;
    private Map headers = new HashMap();
    private boolean xop;
    
    public SimpleAttachment(String id, DataHandler handler)
    {
        this.id = id;
        this.handler = handler;
    }

    public String getId()
    {
        return id;
    }

    public DataHandler getDataHandler()
    {
        return handler;
    }

    public void setHeader(String name, String value)
    {
        headers.put(name, value);
    }
    
    public String getHeader(String name)
    {
        return (String) headers.get(name);
    }

    public boolean isXOP()
    {
        return xop;
    }

    public void setXOP(boolean xop)
    {
        this.xop = xop;
    }
}
