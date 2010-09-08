package org.codehaus.xfire.exchange;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.soap.SoapVersionFactory;
import org.codehaus.xfire.transport.Channel;
import org.jdom.Element;

public abstract class AbstractMessage
{
    public static final String ANONYMOUS_URI = "urn:codehaus:xfire:anonymous";

    private Element header;
    private String uri;
    private String encoding = "UTF-8";
    private Object body;
    private Attachments attachments;
    private SoapVersion soapVersion = Soap11.getInstance();
    private Channel channel;
    private Map properties = new HashMap();

    public Object getBody()
    {
        return body;
    }

    public void setBody(Object body)
    {
        this.body = body;
    }

    public Channel getChannel()
    {
        return channel;
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public SoapVersion getSoapVersion()
    {
        return soapVersion;
    }

    public void setSoapVersion(String soapVersion)
    {
        this.soapVersion = SoapVersionFactory.getInstance().getSoapVersion(soapVersion);
    }

    public void setSoapVersion(SoapVersion soapVersion)
    {
        this.soapVersion = soapVersion;
    }
    
    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public boolean hasHeader()
    {
        return header != null;
    }
    
    public Element getHeader()
    {
        return header;
    }
    
    public Element getOrCreateHeader()
    {
        if (header == null)
        {
            header = new Element("Header", getSoapVersion().getPrefix(), getSoapVersion().getNamespace());
        }
        return header;
    }

    public void setHeader(Element header)
    {
        this.header = header;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public Attachments getAttachments()
    {
        return attachments;
    }

    public void setAttachments(Attachments attachments)
    {
        this.attachments = attachments;
    }

    public Object getProperty(Object key)
    {
        return properties.get(key);
    }

    public void setProperty(Object key, Object value)
    {
        properties.put(key, value);
    }
    
    public String toString()
    {
        return super.toString() + "[uri=\"" + getUri() + "\"]"; 
    }
}
