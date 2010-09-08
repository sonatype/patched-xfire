package org.codehaus.xfire.service.invoker;

import java.lang.reflect.Modifier;

import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.factory.Factory;

/**
 * The XFire implementation of {@link Factory} interface.
 * <p>
 * @author Ben Yu
 * Feb 10, 2006 11:31:27 PM
 */
public class LocalFactory implements Factory {
  private final ThreadLocal local = new ThreadLocal();
  private final String service_class_key; 
  public Service getService(){
    return (Service)local.get();
  }
  public void setService(Service service){
    this.local.set(service);
  }
  public Object create() throws XFireFault {
    final Service service = getService();
    if(service==null){
      throw new XFireFault("service not initialized yet!", XFireFault.RECEIVER);
    }
    try
    {
        Class svcClass = (Class) service.getProperty(service_class_key);

        if (svcClass == null)
        {
            svcClass = service.getServiceInfo().getServiceClass();
            if(svcClass.isInterface())
            {
                throw new XFireFault(service_class_key + " not set for interface '" + svcClass.getName() + "'", XFireFault.RECEIVER);
            }
        }
      
        if(svcClass.isInterface())
        {
            throw new XFireFault("Service class '" + svcClass.getName() + "' is an interface", XFireFault.RECEIVER);
        }
      
      if(Modifier.isAbstract(svcClass.getModifiers()))
      {
          throw new XFireFault("Service class '" + svcClass.getName() + "' is abstract", XFireFault.RECEIVER);
      }
        return svcClass.newInstance();
    }
    catch (InstantiationException e)
    {
        throw new XFireFault("Couldn't instantiate service object.", e, XFireFault.RECEIVER);
    }
    catch (IllegalAccessException e)
    {
        throw new XFireFault("Couldn't access service object.", e, XFireFault.RECEIVER);
    }
  }
  public LocalFactory(final String service_class_key) {
    this.service_class_key = service_class_key;
  }
  public LocalFactory(final String service_class_key, Service service){
    this.service_class_key = service_class_key;
    setService(service);
  }
}
