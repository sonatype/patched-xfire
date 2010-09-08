package org.codehaus.xfire.transport.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.soap.SoapTransportHelper;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.DefaultEndpoint;
import org.codehaus.xfire.transport.MapSession;
import org.codehaus.xfire.transport.Session;

/**
 * A transport which passes messages via the JVM.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class LocalTransport
    extends AbstractTransport
    implements SoapTransport
{
    private static final Log log = LogFactory.getLog( LocalTransport.class );

    public final static String BINDING_ID = "urn:xfire:transport:local";
    public final static String URI_PREFIX = "xfire.local://";
    private Session session;
    private boolean maintainSession;

    public LocalTransport()
    {
        super();

        SoapTransportHelper.createSoapTransport(this);
    }

    protected Channel createNewChannel( String uri )
    {
        log.debug( "Creating new channel for uri: " + uri );

        LocalChannel c = new LocalChannel( uri, this, session );
        c.setEndpoint( new DefaultEndpoint() );

        return c;
    }

    public void setMaintainSession( boolean maintainSession )
    {
        this.maintainSession = maintainSession;
        resetSession();
    }

    public void resetSession()
    {
        if( maintainSession )
        {
            session = new MapSession();
        }
        else
        {
            session = null;
        }
    }

    protected String getUriPrefix()
    {
        return URI_PREFIX;
    }

    public String[] getSupportedBindings()
    {
        return new String[] { BINDING_ID };
    }

    public String[] getKnownUriSchemes()
    {
        return new String[]{ URI_PREFIX };
    }

    public String getName()
    {
        return "Local";
    }

    public String[] getSoapTransportIds()
    {
        return new String[] { BINDING_ID };
    }
}