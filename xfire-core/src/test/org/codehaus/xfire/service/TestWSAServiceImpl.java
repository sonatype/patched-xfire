package org.codehaus.xfire.service;

import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.XFireFault;
import org.jdom.Element;

public class TestWSAServiceImpl
    implements TestWSAService
{

    public Element echo(Element msg) throws XFireFault
    {
        if( msg == null || msg.getValue() == null || msg.getValue().length() == 0 ){
           // throw new RuntimeException("echo:EmptyEchoString");
            QName n = new QName("dd");
            
            XFireFault fault  = new XFireFault("msg",new QName("echo:EmptyEchoString"));
           //fault.addNamespace("echo","http://example.org/echo");
            throw fault;
        }
        
        return msg;
    }

}
