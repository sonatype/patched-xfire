package org.codehaus.xfire.service;

import org.codehaus.xfire.fault.XFireFault;
import org.jdom.Element;

/**
 * Throws an exception while echoing.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public class BadEcho
    implements Echo
{
    public Element echo(Element e) 
        throws XFireFault
    {
        throw new XFireFault("Fault!", XFireFault.SENDER);
    }
}