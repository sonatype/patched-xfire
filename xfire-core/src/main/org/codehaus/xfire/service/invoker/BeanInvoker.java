package org.codehaus.xfire.service.invoker;

import org.codehaus.xfire.MessageContext;

/**
 * Invoker for externally created service objects.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:ajoo.email@gmail.com">Ben Yu</a>
 * @since Feb 9, 2005
 */
public class BeanInvoker
        extends AbstractInvoker
{
    private Object proxy;

    public BeanInvoker(Object proxy)
    {
        this.proxy = proxy;
    }

    public Object getServiceObject(MessageContext context) 
    {
        return proxy;
    }
}
