package org.codehaus.xfire.service.invoker;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Session;
import org.codehaus.xfire.util.factory.Factory;
import org.codehaus.xfire.util.factory.Pool;
import org.codehaus.xfire.util.factory.PooledFactory;
import org.codehaus.xfire.util.factory.SimplePool;


/**
 * This scope policy implements one servant instance per session.
 * <p>
 * @author Ben Yu
 * Feb 6, 2006 11:41:08 AM
 */
public class SessionScopePolicy implements ScopePolicy {
  /**
   * Get the key for caching a service.
   * @param service the service.
   * @return the key.
   */
  protected Object getServiceKey(Service service){
    return "service." + service.getSimpleName();
  }
  public Factory applyScope(Factory f, MessageContext ctxt) {
    return new PooledFactory(f, 
        getSessionScope(getServiceKey(ctxt.getService()), ctxt.getSession()));
  }
  public String toString(){
    return "session scope";
  }
  private static Pool getSessionScope(final Object key, final Session session){
    return new SimplePool(){
      public Object get() {
        return session.get(key);
      }
      public void set(Object val) {
        session.put(key, val);
      }
      public String toString(){
        return "session scope";
      }
      /* This is not guaranteed to be safe with concurrent access to HttpSession.
       * But better than nothing.
       * @see jfun.yan.SimplePool#getMutex()
       */
      protected Object getMutex(){
        return Service.class;
      }
    };
  }
  private static SessionScopePolicy singleton = new SessionScopePolicy();
  public static ScopePolicy instance(){
    return singleton;
  }
}
