package org.codehaus.xfire.examples.router;
// START SNIPPET: handler
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.Service;
import org.jdom.Element;
import org.jdom.Namespace;

public class ServiceRouterHandler
    extends AbstractHandler
{
    public final static String VERSION_NS = "http://xfire.codehaus.org/examples/router";
    public final static String VERSION_NAME = "Version";

    
    public ServiceRouterHandler() 
    {
        super();
        setPhase(Phase.PRE_DISPATCH);
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        Element header = context.getInMessage().getHeader();
        if (header == null) return;
        
        Element versionEl = header.getChild(VERSION_NAME, Namespace.getNamespace(VERSION_NS));
        if (versionEl == null) return;
        
        String version = versionEl.getValue();
        if (version == null || version.length() == 0)
        {
            throw new XFireFault("An empty version element is not allowed.", XFireFault.SENDER);
        }
        
        setVersion(version, context);
    }

    /**
     * Looks up the appropriate service version using referenced by "Echo" plus the version string.
     */
    private void setVersion(String version, MessageContext context) 
        throws XFireFault
    {
        Service service = context.getXFire().getServiceRegistry().getService("Echo" + version);
        
        if (service == null)
        {
            throw new XFireFault("Invalid version: " + version, XFireFault.SENDER);
        }
        
        context.setService(service);
   }
}
// END SNIPPET: handler
