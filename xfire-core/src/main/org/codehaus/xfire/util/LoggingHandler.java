package org.codehaus.xfire.util;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.w3c.dom.Document;

/**
 * Logs a message to a commons logging Log at the INFO level. This requires DOM
 * to be activated for the particular flow. This can be done with the DOMInHandler
 * or DOMOutHandler.
 * 
 * @see org.codehaus.xfire.util.dom.DOMOutHandler
 * @see org.codehaus.xfire.util.dom.DOMInHandler
 * @author Dan Diephouse
 */
public class LoggingHandler
    extends AbstractHandler
{
    private static final Log log = LogFactory.getLog(LoggingHandler.class);
    
    public void invoke(MessageContext context)
        throws Exception
    {
        Document doc = (Document) context.getCurrentMessage().getProperty(DOMInHandler.DOM_MESSAGE);
        
        if (doc == null)
        {
            log.error("DOM Document was not found so the message could not be logged. " +
                    "Please add DOMInHandler/DOMOutHandler to your flow!");
            return;
        }
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DOMUtils.writeXml(doc.getDocumentElement(), bos);
        
        log.info(bos.toString());
    }
}