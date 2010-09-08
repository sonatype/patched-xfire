package org.codehaus.xfire.server.http;

import java.io.File;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.server.XFireServer;
import org.codehaus.xfire.transport.http.XFireServlet;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * HTTP Server for XFire services.
 * 
 * 
 */
public class XFireHttpServer
    implements XFireServer
{
    // components
    private Server httpServer;

    // properties
    private int port = 8081;

    private XFire xfire;

    public XFireHttpServer() {}
    
    private File keystore;
    private String keystorePassword;
    private String keyPassword;
    
    public XFireHttpServer(File keystore, String keystorePassword, String keyPassword) {
        this(XFireFactory.newInstance().getXFire(), keystore, keystorePassword, keyPassword);
    }
    
    public XFireHttpServer(XFire xfire, File keystore, String keystorePassword, String keyPassword) {
        this.xfire = xfire;
        this.keystore = keystore;
        this.keystorePassword = keystorePassword;
        this.keyPassword = keyPassword;
    }
    
    public XFireHttpServer(XFire xfire) 
    {
        this.xfire = xfire;
    }
    
    public void start()
        throws Exception
    {
        if (isStarted()) {
            return;
        }
        
        httpServer = new Server();
        
        if (keystore != null)
        {
        	SslSocketConnector sslConector  = new SslSocketConnector();
        	sslConector.setPort(port);
        	sslConector.setKeystore(keystore.getAbsolutePath());
        	sslConector.setPassword(keystorePassword);
        	sslConector.setKeyPassword(keyPassword);
        	httpServer.addConnector(sslConector);
          
        }
        else
        {   
        	 Connector connector=new SocketConnector();
             connector.setPort(port);
             httpServer.addConnector(connector);
        	
        }
        RequestLogHandler loger = new RequestLogHandler();
        loger.setRequestLog(null);
        
        Context context = new Context(httpServer,"/",Context.SESSIONS);
        context.setEventListeners(null);
        context.addHandler(loger);
        
        ServletHolder servlet = new ServletHolder(new XFireServlet());
        context.addServlet(servlet, "/*");
                
        if (xfire != null){
        	context.setAttribute(XFireServlet.XFIRE_INSTANCE, xfire);
         }
            
        httpServer.start();
    }

    public void stop()
        throws Exception
    {
        if (isStarted())
        {
            httpServer.stop();
            httpServer = null;
        }
    }

    public boolean isStarted()
    {
        return (httpServer != null) && httpServer.isStarted();
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int value)
    {
        port = value;
    }

    public Server getServer(){
    	return httpServer;
    }
}
