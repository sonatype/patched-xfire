package org.codehaus.xfire.transport.dead;

import org.codehaus.xfire.soap.SoapTransportHelper;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Channel;

/**
 * A place for messages which cannot be routed to a destination.
 *  
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DeadLetterTransport
    extends AbstractTransport
{
    public final static String NAME = "dead-letter-transport";
    public static final String DEAD_LETTER_URI = "xfire.dead://";
    
    public DeadLetterTransport()
    {
        SoapTransportHelper.createSoapTransport(this);
    }

    protected Channel createNewChannel(String uri)
    {
        return new DeadLetterChannel(this);
    }

    protected String getUriPrefix()
    {
        return DEAD_LETTER_URI;
    }

    public String getName()
    {
        return NAME;
    }

    public String[] getKnownUriSchemes()
    {
        return new String[] { DEAD_LETTER_URI };
    }
}
