package org.codehaus.xfire.attachments;

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataContentHandler;


/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class AbstractDataContentHandlerFactory
    implements javax.activation.DataContentHandlerFactory
{    
    private Map types = new HashMap();
    private Map classToHandlers = new HashMap();
    private Map classToType = new HashMap();
    
    public DataContentHandler createDataContentHandler(String contentType)
    {
        return (DataContentHandler) types.get(contentType);
    }
    
    public DataContentHandler getDataContentHandler(Class clazz)
    {
        return (DataContentHandler) classToHandlers.get(clazz);
    }
    
    public String getContentType(Class clazz)
    {
        return (String) classToHandlers.get(clazz);
    }
    
    /**
     * Register a DataContentHandler for a particular MIME type.
     * @param contentType The Content Type.
     * @param handler The DataContentHandler.
     */
    public void register(String contentType, Class clazz, DataContentHandler handler)
    {
        types.put(contentType, handler);
        classToHandlers.put(clazz, handler);
        classToType.put(clazz, contentType);
    }
}
