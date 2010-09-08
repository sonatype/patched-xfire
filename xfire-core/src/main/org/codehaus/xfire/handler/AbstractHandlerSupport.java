package org.codehaus.xfire.handler;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.AbstractContext;

public abstract class AbstractHandlerSupport
    extends AbstractContext
    implements HandlerSupport
{
    private List inHandlers = new ArrayList();
    private List outHandlers = new ArrayList();
    private List faultHandlers = new ArrayList();

    public void addFaultHandler(Handler handler)
    {
        faultHandlers.add(handler);
    }
    
    public List getFaultHandlers()
    {
        return faultHandlers;
    }

    public void setFaultHandlers(List faultHandlers)
    {
        this.faultHandlers = faultHandlers;
    }

    public void addInHandler(Handler handler)
    {
        inHandlers.add(handler);
    }
    
    public List getInHandlers()
    {
        return inHandlers;
    }

    public void setInHandlers(List inHandlers)
    {
        this.inHandlers = inHandlers;
    }

    public void addOutHandler(Handler handler)
    {
        outHandlers.add(handler);
    }
    
    public List getOutHandlers()
    {
        return outHandlers;
    }

    public void setOutHandlers(List outHandlers)
    {
        this.outHandlers = outHandlers;
    }
}
