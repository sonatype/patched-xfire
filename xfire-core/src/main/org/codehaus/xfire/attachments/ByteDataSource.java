package org.codehaus.xfire.attachments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public class ByteDataSource
    implements DataSource
{
    private String contentType;
    private String name;
    private byte[] data;
    private int offset;
    private int length;
    
    public ByteDataSource(byte[] data)
    {
        this(data, 0, data.length);
    }

    public ByteDataSource(byte[] data, int offset, int length)
    {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getContentType()
    {
        return contentType;
    }

    public InputStream getInputStream()
        throws IOException
    {
        return new ByteArrayInputStream(data, offset, length);
    }

    public String getName()
    {
        return name;
    }

    public OutputStream getOutputStream()
        throws IOException
    {
        return null;
    }

}
