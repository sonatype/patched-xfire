package org.codehaus.xfire.client;

import java.util.Collection;

import org.codehaus.xfire.MessageContext;

public interface Correlator
{
    public Invocation correlate(MessageContext context, Collection invocations);
}
