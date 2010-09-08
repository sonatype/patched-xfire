package org.codehaus.xfire.service;

import org.codehaus.xfire.fault.XFireFault;
import org.jdom.Element;

/**
 * A handler which echoes the SOAP Body back.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public interface Echo
{
    public Element echo(Element e) throws XFireFault;
}