package org.codehaus.xfire.attachments;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * This test exercises more the Activation framework than XFire itself.
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class DataHandlerTest
    extends AbstractXFireTest
{
    // on some Linux systems, file type mapping is stored in a location
    // not searched by default, thus breaking this test. Let's override it temporarily
    private static final FileTypeMap DEFAULT_FILE_TYPE_MAP = FileTypeMap.getDefaultFileTypeMap();
    private static final FileTypeMap OVERRIDING_FILE_TYPE_MAP = createOverridingFileTypeMap();

    // Note: this is not perfect: in a perfect world /etc/mime.types should be overriden by 
    // the user ~/.mime.types file. Because of what we do here, it won't.
    private static final FileTypeMap createOverridingFileTypeMap()
    {
        File file = new File("/etc/mime.types");
        FileTypeMap result = DEFAULT_FILE_TYPE_MAP;
        if (file.exists())
        {
            try {
                InputStream inputStream = new FileInputStream(file);
                result = new MimetypesFileTypeMap(inputStream);
                inputStream.close();
            } catch(Exception ignored) {
            }
        }
        return result;
    }

    protected void setUp()
    {
        FileTypeMap.setDefaultFileTypeMap(OVERRIDING_FILE_TYPE_MAP);
    }

    protected void tearDown()
    {
        FileTypeMap.setDefaultFileTypeMap(DEFAULT_FILE_TYPE_MAP);
    }


    public void testText()
        throws Exception
    {
        DataSource ds = new FileDataSource(
            getTestFile("src/test/org/codehaus/xfire/attachments/test.txt"));

        DataHandler handler = new DataHandler(ds);

        Object content = handler.getContent();
        assertNotNull(content);
        assertTrue("The content data type was not correctly detected. Check you computer/activation framework setup. " 
                   + "Expected String but got: " + content.getClass(), content instanceof String);
        assertEquals("bleh", content);
    }
    
    public void testImages()
        throws Exception
    {
        DataSource ds = new FileDataSource(
            getTestFile("src/test/org/codehaus/xfire/attachments/xfire_logo.jpg"));
    
        DefaultDataContentHandlerFactory factory = new DefaultDataContentHandlerFactory();

        Object content = factory.createDataContentHandler("image/jpeg").getContent(ds);
        assertNotNull(content);
        assertTrue(content instanceof java.awt.Image);
        
        ds = new FileDataSource(getTestFile("src/test/org/codehaus/xfire/attachments/fax.tif"));
        assertNotNull(ds);
        
        content = factory.createDataContentHandler("image/tiff").getContent(ds);
        assertNotNull(content);
        assertTrue("The content data type was not correctly detected. Check you computer/activation framework setup. " 
                   + "Expected java.awt.Image but got: " + content.getClass(), content instanceof java.awt.Image);
    }
}
