package org.codehaus.xfire.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class HandlerPipeline
    implements Handler
{
    private static final Log log = LogFactory.getLog(HandlerPipeline.class);
    
    private List phases;
    private Map handlers;
    private boolean paused = false;
    private Phase currentPhase;
    
    // Store this as a variable for performance
    private String INVOKED_INTERCEPTORS = this.toString();
    
    public HandlerPipeline(List phases)
    {
        super();
        
        handlers = new HashMap();

        // Order the phases correctly based on priority
        this.phases = phases;
        
        for (Iterator itr = phases.iterator(); itr.hasNext();)
        {
            Phase phase = (Phase) itr.next();
            
            handlers.put(phase.getName(), new HandlerOrderer());
        }
    }
    
    public void addHandlers(List newhandlers)
    {
        if (newhandlers == null) return;
        
        for (Iterator itr = newhandlers.iterator(); itr.hasNext();)
        {
            Handler handler = (Handler) itr.next();

            addHandler(handler);
        }
    }

    public void addHandler(Handler handler)
    {
        if (log.isDebugEnabled())
            log.debug("adding handler " + handler + " to phase " + handler.getPhase());
        
        HandlerOrderer phaseHandlers = getPhaseHandlers(handler.getPhase());
        
        if (phaseHandlers == null) 
        {
            if (log.isDebugEnabled())
                log.debug("Phase " + handler.getPhase() + " does not exist. Skipping handler " + handler.getClass().getName());
        }
        else
        {
            phaseHandlers.insertHandler(handler);
        }
    }

    public HandlerOrderer getPhaseHandlers(String phase)
    {
        return (HandlerOrderer) handlers.get(phase);
    }
    
    /**
     * Invokes each phase's handler in turn.
     * 
     * @param context The context containing current message 
     *      and this <code>HandlerPipeline</code>.
     * @throws Exception
     */
    public void invoke(MessageContext context)
    	throws Exception
    {
        if (paused) return;
        context.setCurrentPipeline(this);
        Stack invoked = (Stack) context.getProperty(INVOKED_INTERCEPTORS);
        
        if (invoked == null) 
        {
            invoked = new Stack();
            context.setProperty(INVOKED_INTERCEPTORS, invoked);
        }

        for (Iterator itr = phases.iterator(); itr.hasNext();)
        {
            Phase phase = (Phase) itr.next();
            
            // If resuming, won't enter phases already completed
            if (currentPhase != null && phase.compareTo(currentPhase) < 0)
                continue;
  
            currentPhase = phase;
 
            if (log.isDebugEnabled())
                log.debug("Invoking phase " + phase.getName());
            
            List phaseHandlers = getPhaseHandlers(phase.getName()).getHandlers();
            for (int i = 0; i < phaseHandlers.size(); i++ )
            {
                Handler h = (Handler) phaseHandlers.get(i);
                
                //If handler instance has been invoked, continue to next handler
                if (invoked.contains(h))
                    continue;
                
                try
                {
                    if (log.isDebugEnabled())
                        log.debug("Invoking handler " + h.getClass().getName() + 
                                  " in phase " + phase.getName());                    
                    h.invoke(context);
                }
                finally
                {
                    // Add the invoked handler to the stack so we can come
                    // back to it later if a fault occurs.
                    invoked.push(h);
                    if (paused) return;
                }
            }
        }
    }
    
    /**
     * Takes a fault, creates a fault message and sends it via the fault channel.
     * 
     * @param fault
     * @param context
     */
    public void handleFault(XFireFault fault, MessageContext context) 
    {
        Stack invoked = (Stack) context.getProperty(this.toString());

		if ( null != invoked )
		{
			while (invoked.size() > 0)
			{
				Handler h = (Handler) invoked.pop();
				h.handleFault(fault, context);
			}
		}
	}
    
    /**
     * Determines whether or not this Pipeline "understands" a particular header.
     * @param name
     * @return true if pipeline understands a header
     */
    public boolean understands(QName name)
    {
        for (Iterator itr = phases.iterator(); itr.hasNext();)
        {
            Phase phase = (Phase) itr.next();
            
            List phaseHandlers = getPhaseHandlers(phase.getName()).getHandlers();
            for (int i = 0; i < phaseHandlers.size(); i++ )
            {
                Handler h = (Handler) phaseHandlers.get(i);
                QName[] understoodQs = h.getUnderstoodHeaders();

                if (understoodQs != null)
                {
                    for (int j = 0; j < understoodQs.length; j++)
                    {
                        if (understoodQs[j].equals(name))
                            return true;
                    }
                }
            }
        }

        return false;
    }
    
    public void pause() {
        paused = true;
    }
    
    public void resume(MessageContext context) throws Exception {
        if (!paused) return;
        
        paused = false;
        invoke(context);
    }

    public List getAfter()
    {
        return null;
    }

    public List getBefore()
    {
        return null;
    }

    public String getPhase()
    {
        return null;
    }

    public String[] getRoles()
    {
        return null;
    }

    public QName[] getUnderstoodHeaders()
    {
        return null;
    }
    
}