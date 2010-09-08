package org.codehaus.xfire.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.codehaus.xfire.XFireRuntimeException;

/**
 * Resolves a File, classpath resource, or URL according to the follow rules:
 * <ul>
 * <li>Check to see if a file exists, relative to the base URI.</li>
 * <li>If the file doesn't exist, check the classpath</li>
 * <li>If the classpath doesn't exist, try to create URL from the URI.</li>
 * </ul>
 * @author Dan Diephouse
 */
public class Resolver
{
    private File file;
    private URI uri;
    private InputStream is;
    private URL url;
    
    public Resolver(String path) throws IOException
    {
        this("", path);
    }
    
    public Resolver(String baseUriStr, String uriStr) 
        throws IOException
    {
        if (uriStr.startsWith("classpath:")) 
        {
            tryClasspath(uriStr);
        }
        else
        {
            tryFileSystem(baseUriStr, uriStr);
        }
        
        if (is == null) 
        {
            String msg = "Could not find resource '" + uriStr;
            if (baseUriStr != null)
                msg += "' relative to '" + baseUriStr + "'";
            
            throw new IOException(msg);
        }
    }

    private void tryFileSystem(String baseUriStr, String uriStr)
        throws IOException, MalformedURLException
    {
    	if (uriStr.startsWith("file:")) 
    	{
    		uriStr = new URL(URLDecoder.decode(uriStr, "utf-8")).getFile();
    	}
    	
        try 
        {
            URI relative;
            File uriFile = new File(uriStr);
            uriFile = new File(uriFile.getAbsolutePath());

            if (uriFile.exists())
                relative = uriFile.toURI();
            else
                relative = new URI(uriStr);

            if (relative.isAbsolute())
            {
                uri = relative;
                url = relative.toURL();
                is = url.openStream();
            }
            else if (baseUriStr != null)
            {
                URI base;
                File baseFile = new File(baseUriStr);
                
                if (!baseFile.exists() && baseUriStr.startsWith("file:/"))
                {
                    baseFile = new File(baseUriStr.substring(6));
                }
                
                if (baseFile.exists())
                    base = baseFile.toURI();
                else
                    base = new URI(baseUriStr);
                
                base = base.resolve(relative);
                if (base.isAbsolute())
                {
                	url = base.toURL();
                    is = url.openStream();
                    uri = base;
                }
            }
        } catch (URISyntaxException e) {
        }
        
        if (uri != null && "file".equals(uri.getScheme()))
        {
            file = new File(uri);
        }
        
        if (is == null && file != null && file.exists()) 
        {
            uri = file.toURI();
            url = file.toURL();
            try
            {
                is = new FileInputStream(file);
            }
            catch (FileNotFoundException e)
            {
                throw new XFireRuntimeException("File was deleted! " + uriStr, e);
            }
        }
        else if (is == null)
        {
            tryClasspath(uriStr);
        }
    }

    private void tryClasspath(String uriStr)
        throws IOException
    {
        if (uriStr.startsWith("classpath:")) 
        {
            uriStr = uriStr.substring(10);
        }
            
        URL url = ClassLoaderUtils.getResource(uriStr, getClass());
        
        if (url == null)
        {
            tryRemote(uriStr);
        }
        else
        {
            try
            {
            	String decodedURL = url.toString();
                uri = new URI(URLEncoder.encode(decodedURL, "UTF-8"));
            }
            catch (URISyntaxException e)
            {
                // this occurs when you have spaces instead of '%20'...
            }
            is = url.openStream();
            this.url = url;
        }
    }

    private void tryRemote(String uriStr)
        throws IOException
    {
        URL url;
        try 
        {
            url = new URL(uriStr);
            this.url = url;
            uri = new URI(url.toString());
            is = url.openStream();
        }
        catch (MalformedURLException e)
        {
        }
        catch (URISyntaxException e)
        {
        }
    }
    
    public URI getURI()
    {
        return uri;
    }
    
    public URL getURL() 
    {
    	return url;
    }
    public InputStream getInputStream()
    {
        return is;
    }
    
    public boolean isFile()
    {
        return file.exists();
    }
    
    public File getFile()
    {
        return file;
    }
}
