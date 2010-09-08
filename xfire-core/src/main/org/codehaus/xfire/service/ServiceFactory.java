package org.codehaus.xfire.service;

import java.net.URL;
import java.util.Map;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface ServiceFactory
{
    /**
     * Create a service from the specified class.
     *
     * @param clazz The service class used to populate the operations and parameters.
     * @return The service.
     */
    public Service create(Class clazz);

    /**
     * Create a service from the specified class.
     *
     * @param clazz The service class used to populate the operations and parameters.
     * @param properties Properties to set on the service and use in construction.
     * @return The service.
     */
    public Service create(Class clazz, Map properties);

    /**
     * Create a service from the specified class.
     * 
     * @param clazz
     *            The service class used to populate the operations and
     *            parameters.
     * @param name
     *            The name of the service.
     * @param namespace
     *            The default namespace of the service.
     * @param properties
     *            Service specific properties which the ServiceFactory will use
     *            to create the service.
     * @return The service.
     */
    public Service create(Class clazz,
                          String name,
                          String namespace,
                          Map properties);
    
    /**
     * Create a service from a WSDL file.
     *
     * @param clazz   The service class for the wsdl.
     * @param wsdlUrl The WSDL URL.
     * @return
     * @throws Exception
     */
    public Service create(Class clazz, QName service, URL wsdlUrl, Map properties);
    
    /**
     * Create a service from a WSDL file.
     *
     * @param clazz   The service class for the wsdl.
     * @param def     The WSDL definition.
     * @return
     * @throws Exception
     */
    public Service create(Class clazz, QName service, Definition def, Map properties);
}
