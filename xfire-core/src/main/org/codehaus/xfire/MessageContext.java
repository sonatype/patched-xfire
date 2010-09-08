package org.codehaus.xfire;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageExchange;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Session;
import org.codehaus.xfire.util.UID;

/**
 * Holds inforrmation about the message request and response.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class MessageContext extends AbstractContext
{
    private Session session;

    private Client client;
    private Service service;
    private Binding binding;
    private MessageExchange exchange;
    private AbstractMessage currentMessage;
    private HandlerPipeline inPipeline;
    private HandlerPipeline outPipeline;
    private HandlerPipeline currentPipeline;
    private Handler faultHandler;
    private XFire xfire;
    private String id;
    
    public MessageContext()
    {
        id = UID.generate();
    }
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public XFire getXFire()
    {
        return xfire;
    }

    public void setXFire(XFire xfire)
    {
        this.xfire = xfire;
    }

    public Binding getBinding()
    {
        return binding;
    }

    public void setBinding(Binding binding)
    {
        this.binding = binding;
    }
    
    public MessageExchange getExchange()
    {
        return exchange;
    }

    public void setExchange(MessageExchange exchange)
    {
        this.exchange = exchange;
    }

    public OutMessage getOutMessage()
    {
        return exchange.getOutMessage();
    }

    public InMessage getInMessage()
    {
        return exchange.getInMessage();
    }

    public AbstractMessage getCurrentMessage()
    {
        return currentMessage;
    }
    

    public void setCurrentMessage(AbstractMessage currentMessage)
    {
        this.currentMessage = currentMessage;
    }
    

    /**
     * The session that this request is a part of.
     *
     * @return
     */
    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

    /**
     * The service being invoked.
     *
     * @return
     */
    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    public Client getClient()
    {
        return client;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }

    public HandlerPipeline getInPipeline()
    {
        return inPipeline;
    }

    public void setInPipeline(HandlerPipeline messagePipeline)
    {
        this.inPipeline = messagePipeline;
    }

    public HandlerPipeline getOutPipeline()
    {
        return outPipeline;
    }

    public void setOutPipeline(HandlerPipeline outPipeline)
    {
        this.outPipeline = outPipeline;
    }

    public HandlerPipeline getCurrentPipeline()
    {
        return currentPipeline;
    }

    public void setCurrentPipeline(HandlerPipeline currentPipeline)
    {
        this.currentPipeline = currentPipeline;
    }

    /**
     * The fault Handler is invoked when a fault incurs in the pipeline.
     * @return
     */
    public Handler getFaultHandler()
    {
        return faultHandler;
    }

    public void setFaultHandler(Handler faultHandler)
    {
        this.faultHandler = faultHandler;
    }

    /**
     * Gets a propert by checking layered contexts. Contexts are checked in the
     * following order:
     * <ul>
     * <li>MessageContext
     * <li>OperationInfo
     * <li>Client
     * <li>Service
     * </li>
     *  
     * @param key
     * @return
     */
    public Object getContextualProperty(String key)
    {
        Object val = getProperty(key);
        
        if (val == null && getExchange() != null && getExchange().getOperation() != null)
        {
            val = getExchange().getOperation().getProperty(key);
        }
        
        if (val == null && getClient() != null)
        {
            val = getClient().getProperty(key);
        }
        
        if (val == null && getService() != null)
        {
            val = getService().getProperty(key);
        }
        
        return val;
    }
}
