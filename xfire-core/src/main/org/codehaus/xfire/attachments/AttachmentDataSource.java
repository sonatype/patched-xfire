package org.codehaus.xfire.attachments;

import org.codehaus.xfire.util.CachedOutputStream;

import javax.activation.DataSource;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;

/**
 * @author <a href="mailto:plightbo@gmail.com">Patrick Lightbody</a>
 */
public class AttachmentDataSource implements DataSource {
    private final String ct;
    private final CachedOutputStream cos;

    public AttachmentDataSource(String ct, CachedOutputStream cos) {
        this.ct = ct;
        this.cos = cos;
    }

    public String getContentType()
    {
        return ct;
    }

    public InputStream getInputStream()
        throws IOException
    {
        return cos.getInputStream();
    }

    public String getName()
    {
        return null;
    }

    public OutputStream getOutputStream()
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public File getFile()
    {
        return cos.getTempFile();
    }
}
