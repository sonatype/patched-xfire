package org.codehaus.xfire.attachments;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataContentHandler;
import javax.activation.DataSource;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class ImageDataContentHandler
    implements DataContentHandler
{
    public Object getContent(DataSource ds)
        throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
    
        copy(ds.getInputStream(), bos, 8096);

        Image image = Toolkit.getDefaultToolkit().createImage(bos.toByteArray());

        return image;
    }
    
    public void copy(final InputStream input,
                     final OutputStream output,
                     final int bufferSize)
        throws IOException
    {
        final byte[] buffer = new byte[bufferSize];

        int n = 0;
        while (-1 != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
        }
    }

    public Object getTransferData(DataFlavor arg0, DataSource arg1)
        throws UnsupportedFlavorException, IOException
    {
        return null;
    }

    public DataFlavor[] getTransferDataFlavors()
    {
        return null;
    }

    public void writeTo(Object obj, String contentTyp, OutputStream out)
        throws IOException
    {
        Image image = (Image) obj;
    }

}
