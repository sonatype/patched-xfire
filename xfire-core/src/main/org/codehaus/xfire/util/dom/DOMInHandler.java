package org.codehaus.xfire.util.dom;

import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.stax.W3CDOMStreamReader;
import org.w3c.dom.Document;

/**
 * Reads the incoming stream to a DOM document and sets the stream to
 * a W3CDOMStreamReader.
 * 
 * @author Dan Diephouse
 */
public class DOMInHandler
    extends AbstractHandler
{
    public static final String DOM_MESSAGE = "dom.message";
    
    public DOMInHandler()
    {
        super();
        setPhase(Phase.PARSE);
        before(ReadHeadersHandler.class.getName());
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setNamespaceAware(true);
        dbf.setCoalescing(false);
        
        doc = STAXUtils.read(dbf.newDocumentBuilder(), context.getInMessage().getXMLStreamReader(), false);
        
        context.getInMessage().setProperty(DOM_MESSAGE, doc);
        context.getInMessage().setXMLStreamReader(new W3CDOMStreamReader(doc.getDocumentElement()));
    }
}
