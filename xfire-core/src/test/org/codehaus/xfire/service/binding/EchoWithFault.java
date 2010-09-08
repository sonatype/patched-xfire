package org.codehaus.xfire.service.binding;

/**
 * Echo
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class EchoWithFault
{
    public String echo( String echo ) throws EchoFault
    {
        return echo;
    }
}
