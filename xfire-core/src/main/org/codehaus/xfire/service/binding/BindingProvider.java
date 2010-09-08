package org.codehaus.xfire.service.binding;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.wsdl.SchemaType;

/**
 * A BindingProvider provides the ability to map XML and java objects. This can
 * come in the form of simple POJOs or a DOM tree.  To use one must just implement
 * the interface and provide it to the {@link ObjectServiceFactory}.
 * 
 * @author Dan Diephouse
 */
public interface BindingProvider
{
    void initialize(Service service);

    void initialize(Service service, Binding binding);

    /**
     * Gives a binding the chance to suggest a name for a particular parameter.
     * @param m The method of the parameter.
     * @param param The index of the parameter. -1 specifies the return parameter.
     * @return The suggestion. null if there isn't a suggestion.
     */
    QName getSuggestedName(Service service, OperationInfo op, int param);

    Object readParameter(MessagePartInfo p, XMLStreamReader reader, MessageContext context)
        throws XFireFault;

    void writeParameter(MessagePartInfo p,
                        XMLStreamWriter writer,
                        MessageContext context,
                        Object value) throws XFireFault;

    SchemaType getSchemaType(QName name, Service service);
}
