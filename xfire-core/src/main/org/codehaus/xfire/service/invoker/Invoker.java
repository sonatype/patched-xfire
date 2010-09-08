package org.codehaus.xfire.service.invoker;

import java.lang.reflect.Method;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * The invoker class allows you to control how your service class is invoked.
 * For instance, you could supply an alternate instance which used a Factory to
 * create your service instead of XFire instantiating it.
 *  
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 16, 2004
 */
public interface Invoker
{
    Object invoke( Method m, Object[] params, MessageContext context )
    	throws XFireFault;
}
