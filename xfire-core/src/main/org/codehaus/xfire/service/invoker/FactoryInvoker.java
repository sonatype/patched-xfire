package org.codehaus.xfire.service.invoker;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.factory.Factory;


/**
 * This invoker implementation calls a Factory to create the service object
 * and then applies a scope policy for caching.
 * @author Ben Yu
 * Feb 2, 2006 12:55:59 PM
 */
public class FactoryInvoker extends AbstractInvoker {
  private final Factory factory;
  private final ScopePolicy scope;
  /**
   * Create a FactoryInvoker object.
   * @param factory the factory used to create service object.
   * @param scope the scope policy. Null for default.
   */
  public FactoryInvoker(Factory factory, ScopePolicy scope) {
    this.factory = factory;
    this.scope = scope==null?new ApplicationScopePolicy():scope;
  }

  public Object getServiceObject(MessageContext context)
  throws XFireFault{
    try{
      return getScopedFactory(context).create();
    }
    catch(XFireFault e){
      throw e;
    }
    catch(Throwable e){
      throw new XFireFault(e);
    }
  }
  private Factory getScopedFactory(MessageContext context){
    return scope.applyScope(factory, context);
  }
}
