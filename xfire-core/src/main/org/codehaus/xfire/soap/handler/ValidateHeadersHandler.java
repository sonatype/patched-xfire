package org.codehaus.xfire.soap.handler;

import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Validates that headers flagged as "mustUnderstand" are understood.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ValidateHeadersHandler
    extends AbstractHandler
{
    
    public ValidateHeadersHandler() 
    {
        super();
        setPhase(Phase.PRE_INVOKE);
    }

    /**
     * Validates that the mustUnderstand and role headers are processed correctly.
     *
     * @param context
     * @throws XFireFault
     */
    public void invoke(MessageContext context)
        throws Exception
    {
        if (context.getInMessage().getHeader() == null)
            return;

        SoapVersion version = context.getInMessage().getSoapVersion();
        List elements = context.getInMessage().getHeader().getChildren();
        for (int i = 0; i < elements.size(); i++)
        {
            Element e = (Element) elements.get(i);
            String mustUnderstand = e.getAttributeValue("mustUnderstand",
                    Namespace.getNamespace(version.getNamespace()));

            if (mustUnderstand != null && ( mustUnderstand.equals("1") ||  (version == Soap12.getInstance() &&  mustUnderstand.equals("true"))) )
            {
                assertUnderstandsHeader(context, new QName(e.getNamespaceURI(), e.getName()));
            }
        }
    }

    /**
     * Assert that a service understands a particular header.  If not, a fault is thrown.
     *
     * @param context
     * @param name
     * @throws XFireFault
     */
    protected void assertUnderstandsHeader(MessageContext context, QName name)
            throws XFireFault
    {
        if (context.getInPipeline().understands(name))
            return;

        if (context.getOutPipeline().understands(name))
            return;

        // TODO: Check Out pipeline for understanding
        
        throw new XFireFault("Header {" + name.getLocalPart() + "}" + name.getNamespaceURI()
                + " was not undertsood by the service.", XFireFault.MUST_UNDERSTAND);
    }
}
