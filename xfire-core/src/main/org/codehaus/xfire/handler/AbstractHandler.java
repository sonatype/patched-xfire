package org.codehaus.xfire.handler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public abstract class AbstractHandler
    implements Handler
{
    private List before = new ArrayList();
    private List after = new ArrayList();
    private String phase;
    
	public AbstractHandler() 
    {
        super();
        phase = Phase.USER;
    }
    
    /**
     * Returns null by default, indicating that no headers
     * were understood.
     * 
     * @see org.codehaus.xfire.handler.Handler#getUnderstoodHeaders()
     */
    public QName[] getUnderstoodHeaders()
    {
        return null;
    }

    public String[] getRoles()
    {
        return null;
    }
    
    public final String getPhase()
    {
        return phase;
    }
    
    /**
     * Allow user to set the phase of a handler 
     * i.e. via Spring setter injection (XFIRE-226)
     * @param phase
     */
    public void setPhase(String phase)
    {
        this.phase = phase;
    }
    
    /**
     * @see org.codehaus.xfire.handler.Handler#handleFault(java.lang.Exception, org.codehaus.xfire.MessageContext)
     * @param e
     * @param context
     */
    public void handleFault(XFireFault fault, MessageContext context)
    {
    }
    
    public void after(String handler)
    {
        after.add(handler);
    }
    
    public void before(String handler)
    {
        before.add(handler);
    }

    public List getAfter()
    {
        return after;
    }

    public List getBefore()
    {
        return before;
    }

    /**
     * @param after The after to set.
     */
    public void setAfter(List after) {
        if(this.after == null)
        {
            this.after = after;
        }
        else
        {
            this.after.addAll(after);
        }
    }

    /**
     * @param before The before to set.
     */
    public void setBefore(List before) {
        if(this.before == null)
        {
            this.before = before;
        }
        else
        {
            this.before.addAll(before);
        }
    }
    
}
