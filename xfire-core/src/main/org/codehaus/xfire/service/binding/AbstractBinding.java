package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.stax.DepthXMLStreamReader;

public abstract class AbstractBinding
    implements MessageSerializer
{
    private static final QName XSD_ANY = new QName(SoapConstants.XSD, "anyType", SoapConstants.XSD_PREFIX);

    public void setOperation(OperationInfo operation, MessageContext context)
    {
        context.getExchange().setOperation(operation);
    }

    protected void nextEvent(XMLStreamReader dr)
    {
        try
        {
            dr.next();
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Couldn't parse stream.", e);
        }
    }

    protected OperationInfo findOperation(Collection operations, List parameters)
    {
        // first check for exact matches
        for ( Iterator itr = operations.iterator(); itr.hasNext(); )
        {
            OperationInfo o = (OperationInfo) itr.next();
            List messageParts = o.getInputMessage().getMessageParts();
            if ( messageParts.size() == parameters.size() )
            {
                if (checkExactParameters(messageParts, parameters))
                    return o;
            }
        }
        
        // now check for assignable matches
        for ( Iterator itr = operations.iterator(); itr.hasNext(); )
        {
            OperationInfo o = (OperationInfo) itr.next();
            List messageParts = o.getInputMessage().getMessageParts();
            if ( messageParts.size() == parameters.size() )
            {
                if (checkParameters(messageParts, parameters))
                    return o;
            }
        }        
        return null;
    }
    
    /**
     * Return true only if the message parts exactly match the classes of the parameters
     * @param messageParts
     * @param parameters
     * @return
     */
    private boolean checkExactParameters(List messageParts, List parameters)
    {
        Iterator messagePartIterator = messageParts.iterator();
        for (Iterator parameterIterator = parameters.iterator(); parameterIterator.hasNext();)
        {
            Object param = parameterIterator.next();
            MessagePartInfo mpi = (MessagePartInfo) messagePartIterator.next();            
            if (!mpi.getTypeClass().equals(param.getClass()))
            {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkParameters(List messageParts, List parameters)
    {
        Iterator messagePartIterator = messageParts.iterator();
        for (Iterator parameterIterator = parameters.iterator(); parameterIterator.hasNext();)
        {
            Object param = parameterIterator.next();
            MessagePartInfo mpi = (MessagePartInfo) messagePartIterator.next();
            
            if (!mpi.getTypeClass().isAssignableFrom(param.getClass()))
            {
                if (!param.getClass().isPrimitive() && mpi.getTypeClass().isPrimitive())
                {
                    return checkPrimitiveMatch(mpi.getTypeClass(), param.getClass());
                }
                else
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkPrimitiveMatch(Class clazz, Class typeClass)
    {
        if ((typeClass == Integer.class && clazz == int.class) ||
                (typeClass == Double.class && clazz == double.class) ||
                (typeClass == Long.class && clazz == long.class) ||
                (typeClass == Float.class && clazz == float.class) ||
                (typeClass == Short.class && clazz == short.class) ||
                (typeClass == Boolean.class && clazz == boolean.class) ||
                (typeClass == Byte.class && clazz == byte.class))
            return true;
        
        return false;
    }

    protected MessagePartInfo findMessagePart(MessageContext context, 
                                              Collection operations, 
                                              QName name,
                                              int index)
    {
        // TODO: This isn't too efficient. we need to only look at non headers here
        // TODO: filter out operations which aren't applicable
        MessagePartInfo lastChoice = null;
        for ( Iterator itr = operations.iterator(); itr.hasNext(); )
        {
            OperationInfo op = (OperationInfo) itr.next();
            MessageInfo msgInfo = null;
            if (isClientModeOn(context))
            {
                msgInfo = op.getOutputMessage();
            }
            else
            {
                msgInfo = op.getInputMessage();
            }

            Collection bodyParts = msgInfo.getMessageParts();
            if (bodyParts.size() == 0 || bodyParts.size() <= index) 
            {
                // itr.remove();
                continue;
            }
            
            MessagePartInfo p = (MessagePartInfo) msgInfo.getMessageParts().get(index);
            if (p.getName().equals(name)) return p;

            if (p.getSchemaType().getSchemaType().equals(XSD_ANY))
                lastChoice = p;
        }
        return lastChoice;
    }

    protected void read(InMessage inMessage, MessageContext context, Collection operations)
        throws XFireFault
    {
        List parameters = new ArrayList();
        OperationInfo opInfo = context.getExchange().getOperation();
        
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getInMessage().getXMLStreamReader());
        int param = 0;
        boolean clientMode = isClientModeOn(context);
        
        while (STAXUtils.toNextElement(dr))
        {
            MessagePartInfo p;
            
            if (opInfo != null && clientMode)
            {
                p = (MessagePartInfo) opInfo.getOutputMessage().getMessageParts().get(param);
            }
            else if (opInfo != null && !clientMode)
            {
                p = (MessagePartInfo) opInfo.getInputMessage().getMessageParts().get(param);
            }
            else
            {
                // Try to intelligently find the right part if we don't know the operation yet.
                p = findMessagePart(context, operations, dr.getName(), param);
            }
            
            if (p == null)
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }

            param++;
            parameters.add( context.getService().getBindingProvider().readParameter(p, dr, context) );
            
            if (dr.getEventType() == XMLStreamReader.END_ELEMENT) nextEvent(dr);
        }

        if (opInfo == null && !clientMode)
        {
            opInfo = findOperation(operations, parameters);

            if (opInfo == null)
            {
                StringBuffer sb = new StringBuffer("Could not find appropriate operation for request ");
                //we know we have at least one operation, right?
                sb.append(((OperationInfo)operations.iterator().next()).getName());
                sb.append('(');
                for(Iterator iterator = parameters.iterator(); iterator.hasNext();)
                {
                    sb.append(iterator.next().getClass().getName());
                    if(iterator.hasNext())
                    {
                        sb.append(", ");
                    }
                }
                sb.append(") in service '");
                sb.append(context.getService().getSimpleName());
                sb.append('\'');
                throw new XFireFault(sb.toString(), XFireFault.SENDER);
            }
            
            setOperation(opInfo, context);
        }
        
        context.getInMessage().setBody(parameters);
    }

    public static void writeParameter(XMLStreamWriter writer, 
                                      MessageContext context, 
                                      Object value, 
                                      MessagePartInfo p,
                                      String ns)
        throws XFireFault, XMLStreamException
    {
        
        // write the parameter's start element
        if (p.getSchemaType().isWriteOuter())
        {     
            if (ns.length() > 0)
            {
                String prefix = writer.getPrefix(ns);
                boolean declare = false;
                if (prefix == null || "".equals(prefix) )
                {
                    prefix = "";
                    declare = true;
                    writer.setDefaultNamespace(ns);
                }
                
                writer.writeStartElement(prefix, p.getName().getLocalPart(), ns);
                if (declare) writer.writeDefaultNamespace(ns);
            }
            else
            {
                writer.writeStartElement(p.getName().getLocalPart());
                writer.writeDefaultNamespace("");
            }
        }

        context.getService().getBindingProvider().writeParameter(p, writer, context, value);

        // write the parameter's end element
        if (p.getSchemaType().isWriteOuter())
        {     
            writer.writeEndElement();
        }
    }

    protected Object getParam(Object[] values, MessagePartInfo outParam, MessageContext context)
    {
        int index = outParam.getIndex();
        if (index == -1) return values[0];
        
        Object[] inParams = (Object[]) context.getInMessage().getBody();
        return inParams[index];
    }

    protected Object getClientParam(Object[] values, MessagePartInfo outParam, MessageContext context) 
        throws XFireFault
    {
        if (outParam.getIndex() >= values.length) 
        {
            throw new XFireFault("Not enough input parameters were supplied!", XFireFault.SENDER);
        }
        
        return values[outParam.getIndex()];
    }

    /**
     * Get the namespace for a particular part. This will change depending on if
     * we're doc/lit or rpc/lit or if the MessagePartInfo is a concrete type.
     * 
     * @param context
     * @param p
     * @return
     */
    protected String getBoundNamespace(MessageContext context, MessagePartInfo p)
    {
        if (p.isSchemaElement())
            return p.getName().getNamespaceURI();
        else
            return context.getService().getTargetNamespace();
    }

    public static boolean isClientModeOn(MessageContext context)
    {
        Boolean on = (Boolean) context.getProperty(Client.CLIENT_MODE);
        
        return (on != null && on.booleanValue());
    }
    
    public static MessageInfo getIncomingMessageInfo(MessageContext context)
    {
        MessageInfo msgInfo;
        if (isClientModeOn(context))
        {
            msgInfo = context.getExchange().getOperation().getOutputMessage();
        }
        else
        {
            msgInfo = context.getExchange().getOperation().getInputMessage();
        }
        
        return msgInfo;
    }
    
    public static MessageInfo getOutgoingMessageInfo(MessageContext context)
    {
        MessageInfo msgInfo;
        if (isClientModeOn(context))
        {
            msgInfo = context.getExchange().getOperation().getInputMessage();
        }
        else
        {
            msgInfo = context.getExchange().getOperation().getOutputMessage();
        }
        
        return msgInfo;
    }
}
