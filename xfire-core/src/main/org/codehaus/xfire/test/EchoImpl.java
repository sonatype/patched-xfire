package org.codehaus.xfire.test;

/**
 * Echo
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
// START SNIPPET: service
public class EchoImpl
        implements Echo
{
    public String echo(String echo)
    {
        return echo;
    }
}
// END SNIPPET: service