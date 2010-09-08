package org.codehaus.xfire.wsdl;

import org.codehaus.xfire.XFireException;

public class WSDLBuildingException
    extends XFireException
{
    public WSDLBuildingException(String string)
    {
        super(string);
    }

    public WSDLBuildingException()
    {
        super();
    }

    public WSDLBuildingException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
