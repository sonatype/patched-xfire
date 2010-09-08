package org.codehaus.xfire.handler;

import java.util.List;

/**
 * Provides handlers to an invocation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface HandlerSupport
{
    List getInHandlers();
    
    List getOutHandlers();
    
    List getFaultHandlers();
}
