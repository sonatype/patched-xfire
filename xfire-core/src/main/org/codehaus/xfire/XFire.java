package org.codehaus.xfire;

import java.io.OutputStream;
import java.util.List;

import org.codehaus.xfire.handler.HandlerSupport;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;

/**
 * <p>Central processing point for XFire. This can be instantiated programmatically by using one of the implementations
 * (such as <code>DefaultXFire</code> or can be managed by a container like Pico or Plexus. </p>
 * <p/>
 * Central, however, does not mean that there can be only one. Implementations can be very lightweight, creating fast
 * generic SOAP processors. </p>
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public interface XFire extends HandlerSupport
{
    final public static String ROLE = XFire.class.getName();
    final public static String XFIRE_HOME = "xfire.home";
    final public static String STAX_INPUT_FACTORY="xfire.stax.input.factory";
    final public static String STAX_OUTPUT_FACTORY="xfire.stax.output.factory";
    
    final public static String SERVICES_LIST_DISABLED="services.list.disabled";
    /**
     * Generate WSDL for a service.
     *
     * @param service The name of the service.
     * @param out     The OutputStream to write the WSDL to.
     */
    void generateWSDL(String service, OutputStream out);

    /**
     * Get the <code>ServiceRegistry</code>.
     */
    ServiceRegistry getServiceRegistry();

    /**
     * Get the <code>TransportManager</code>.
     */
    TransportManager getTransportManager();

    List getInPhases();
    
    List getOutPhases();

    Object getProperty(String key);
    
    void setProperty(String key, Object value);
}