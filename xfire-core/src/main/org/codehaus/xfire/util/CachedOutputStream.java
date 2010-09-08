package org.codehaus.xfire.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.xfire.XFireRuntimeException;

public class CachedOutputStream extends OutputStream
{
    private OutputStream currentStream;
    private int threshold;
    private int totalLength = 0;
    private boolean inmem = false;
    private File tempFile = null;
    private File outputDir;
    
    public CachedOutputStream(int threshold, File outputDir) 
        throws IOException
    {
        this.threshold = threshold;
        this.outputDir = outputDir;
        
        if (threshold <= 0)
        {
            createFileOutputStream();
        }
        else
        {
            currentStream = new ByteArrayOutputStream();
            inmem = true;
        }
    }

    public void close()
        throws IOException
    {
        currentStream.close();
    }

    public boolean equals(Object obj)
    {
        return currentStream.equals(obj);
    }

    public void flush()
        throws IOException
    {
        currentStream.flush();
    }

    public int hashCode()
    {
        return currentStream.hashCode();
    }

    public String toString()
    {
        return currentStream.toString();
    }

    public void write(byte[] b, int off, int len)
        throws IOException
    {
        this.totalLength += len;
        if (inmem && totalLength > threshold) switchToFile();

        currentStream.write(b, off, len);
    }

    private void switchToFile() throws IOException
    {
        byte[] bytes = ((ByteArrayOutputStream) currentStream).toByteArray();
        
        createFileOutputStream();
        
        currentStream.write(bytes);
        inmem = false;
    }

    private void createFileOutputStream()
        throws IOException
    {
        if (outputDir == null)
            tempFile = File.createTempFile("att", "tmp");
        else
            tempFile = File.createTempFile("att", "tmp", outputDir);

        currentStream = new BufferedOutputStream(new FileOutputStream(tempFile));
    }
    

    public void write(byte[] b)
        throws IOException
    {
        this.totalLength += b.length;
        if (inmem && totalLength > threshold) switchToFile();
        
        currentStream.write(b);
    }

    public void write(int b)
        throws IOException
    {
        this.totalLength++;
        if (inmem && totalLength > threshold) switchToFile();
        
        currentStream.write(b);
    }
    
    public File getTempFile()
    {
        return tempFile;
    }
    
    public InputStream getInputStream()
    {
        if (inmem)
        {
            return new ByteArrayInputStream(((ByteArrayOutputStream) currentStream).toByteArray());
        }
        else
        {
            try
            {
                return new DeleteOnCloseFileInputStream(tempFile);
            }
            catch (FileNotFoundException e)
            {
                throw new XFireRuntimeException("Cached file was deleted!!!", e);
            }
        }
    }
    
    public void dispose()
    {
       if (!inmem) tempFile.delete();
    }
}
