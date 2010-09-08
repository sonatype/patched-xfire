package org.codehaus.xfire.addressing;

import org.codehaus.xfire.fault.XFireFault;
import org.jdom.Element;

public interface AddressingHeadersFactory
{
    AddressingHeaders createHeaders(Element root) throws XFireFault;
    
 
    
    EndpointReference createEPR(Element root);
    
    boolean hasHeaders(Element root);
    
    void writeHeaders(Element root, AddressingHeaders headers);
    
    void writeEPR(Element root, EndpointReference epr);

    String getAnonymousUri();
    
    String getNoneUri();
}
