package org.codehaus.xfire.server;

/**
 * Common interface for all kind of XFire servers.
 * 
 * @see org.codehaus.xfire.server.http.XFireHttpServer
 * 
 * @version $Id$
 */
public interface XFireServer
{

    public void start() throws Exception;

    public void stop() throws Exception;

    public boolean isStarted();

}
