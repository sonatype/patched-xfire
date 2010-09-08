package org.codehaus.xfire.wsdl11.parser;

import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;

public class DefinitionsHelper
{
    public static SOAPBody getSOAPBody(List extensibilityElements)
    {
        SOAPBody body = null;
        for (int j = 0; j < extensibilityElements.size(); j++)
        {
            Object element = extensibilityElements.get(j);
            if (element instanceof SOAPBody)
            {
                body = (SOAPBody) element;
                break;
            }
        }
        return body;
    }

    public static SOAPBinding getSOAPBinding(Binding binding)
    {
        SOAPBinding soapBinding = null;
        List extensibilityElements = binding.getExtensibilityElements();
        for (int i = 0; i < extensibilityElements.size(); i++)
        {
            Object element = extensibilityElements.get(i);
            if (element instanceof SOAPBinding)
            {
                soapBinding = (SOAPBinding) element;
            }
        }
        return soapBinding;
    }

    public static SOAPAddress getSOAPAddress(Port port)
    {
        SOAPAddress soapAddress = null;
        List extensibilityElements = port.getExtensibilityElements();
        for (int i = 0; i < extensibilityElements.size(); i++)
        {
            Object element = extensibilityElements.get(i);
            if (element instanceof SOAPAddress)
            {
                soapAddress = (SOAPAddress) element;
            }
        }
        return soapAddress;
    }
    
    public static SOAPOperation getSOAPOperation(BindingOperation operation)
    {
        SOAPOperation soapOp = null;
        List extensibilityElements = operation.getExtensibilityElements();
        for (int i = 0; i < extensibilityElements.size(); i++)
        {
            Object element = extensibilityElements.get(i);
            if (element instanceof SOAPOperation)
            {
                soapOp = (SOAPOperation) element;
            }
        }
        return soapOp;
    }
}
