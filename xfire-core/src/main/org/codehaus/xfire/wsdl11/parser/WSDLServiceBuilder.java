package org.codehaus.xfire.wsdl11.parser;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAll;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroupBase;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartContainer;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.wsdl.SchemaType;
import org.codehaus.xfire.wsdl11.ResolverWSDLLocator;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Builds a collection of Services from a WSDL.
 * 
 * @author Dan Diephouse
 * @see org.codehaus.xfire.service.Service
 */
public class WSDLServiceBuilder
{
	public static final String WRAPPED_TYPE = "wrapped.type";
	
    private static final Log log = LogFactory.getLog(WSDLServiceBuilder.class);
    
    private PortType portType;
    private OperationInfo opInfo;
    private XmlSchemaCollection schemas = new XmlSchemaCollection();;
    private boolean isWrapped = false;
    private boolean forceBare = false;
    private BindingProvider bindingProvider;
    
    protected final Definition definition;

    private List bindingAnnotators = new ArrayList();
    
    private Map portType2serviceInfo = new HashMap();
    private Map wop2op = new HashMap();
    private Map winput2msg = new HashMap();
    private Map woutput2msg = new HashMap();
    private Map wfault2msg = new HashMap();
    
    private List schemaInfos = new ArrayList();
    private List definitions = new ArrayList();
    private List definitionPaths = new ArrayList();
    private List portTypes = new ArrayList();
    private Map types = new HashMap();
    private List wsdlServices = new ArrayList();

    /** A collection of XFire Service classes that were built. */
    private Map xFireServices = new HashMap();
    private List allServices = new ArrayList();
    
    private TransportManager transportManager =
        XFireFactory.newInstance().getXFire().getTransportManager();
    private Service service;

    private String systemId;
    
    
    public WSDLServiceBuilder(Definition definition)
    {
        this.definition = definition;
        definitions.add(definition);
        this.systemId = definition.getDocumentBaseURI(); // this is best we have, so use it.
        
        bindingAnnotators.add(new SoapBindingAnnotator());
        
        schemas.setSchemaResolver(new XmlSchemaURIResolver());
    }

    public WSDLServiceBuilder(InputStream is) throws WSDLException
    {
        this("", is);
    }

    public WSDLServiceBuilder(String baseURI, InputStream is) throws WSDLException
    {
        this(WSDLFactory.newInstance().newWSDLReader().readWSDL(new ResolverWSDLLocator(baseURI, new InputSource(is))));
        this.definition.setDocumentBaseURI(baseURI);
        this.systemId = baseURI;
    }
    
    public WSDLServiceBuilder(String baseURI, InputSource source) throws WSDLException
    {
        this(WSDLFactory.newInstance().newWSDLReader().readWSDL(baseURI, source));
        this.definition.setDocumentBaseURI(baseURI);
        this.systemId = source.getSystemId();
    }
    
    public BindingProvider getBindingProvider()
    {
        if (bindingProvider == null)
        {
            try
            {
                bindingProvider = (BindingProvider) ClassLoaderUtils
                        .loadClass("org.codehaus.xfire.aegis.AegisBindingProvider", getClass()).newInstance();
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Couldn't find a binding provider!", e);
            }
        }

        return bindingProvider;
    }
    
    public void setBindingProvider(BindingProvider bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    public Definition getDefinition()
    {
        return definition;
    }

    public List getDefinitions()
    {
        return definitions;
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }

    public void setTransportManager(TransportManager transportManager)
    {
        this.transportManager = transportManager;
    }

    public void build() throws Exception
    {
        processImports(definition);
        
        // Import all the types..
        types.put(systemId, definition.getTypes());
        for (Iterator itr = types.entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();
            visit((String)entry.getKey(), (Types)entry.getValue());
        }
        
        portTypes.addAll(definition.getPortTypes().values());
        for (Iterator itr = portTypes.iterator(); itr.hasNext();)
        {
            portType = (PortType) itr.next();
            visit(portType);
        }
        
        wsdlServices.addAll(definition.getServices().values());
        for (Iterator iterator = wsdlServices.iterator(); iterator.hasNext();)
        {
            javax.wsdl.Service wservice = (javax.wsdl.Service) iterator.next();
            Map portType2Ports = getPortTypeToPortMap(wservice);
            
            for (Iterator ptitr = portType2Ports.entrySet().iterator(); ptitr.hasNext();)
            {
                Map.Entry entry = (Map.Entry) ptitr.next();
                
                PortType portType = (PortType) entry.getKey();
                Collection ports = (Collection) entry.getValue();
                
                if (ports.size() == 0) continue;
                
                ServiceInfo serviceInfo = getServiceInfo(portType);
                WSDLServiceConfigurator config = new WSDLServiceConfigurator(serviceInfo,
                                                                             definition,
                                                                             wservice, 
                                                                             portType,
                                                                             ports,
                                                                             bindingProvider,
                                                                             transportManager);
                config.configure();
                addService(config.getService());
            }
        }
    }

    /**
     * Adds a service to the map of services and also to the list of all services.
     * @param s
     */
    protected void addService(Service s)
    {
        List services = (List) xFireServices.get(s.getName());
        if (services == null)
        {
            services = new ArrayList();
            xFireServices.put(s.getName(), services);
        }
        services.add(s);
        allServices.add(s);
    }

    protected void processImports(Definition parent)
    {
        Collection imports = parent.getImports().values();
        for (Iterator iterator = imports.iterator(); iterator.hasNext();)
        {
            List wsdlImports = (List) iterator.next();
            for (Iterator importItr = wsdlImports.iterator(); importItr.hasNext();)
            {
                Import i = (Import) importItr.next();
                
                Definition iDef = i.getDefinition();
                if (!definitionPaths.contains(i.getLocationURI())) 
                {
                    log.info("Adding wsdl definition " + i.getLocationURI() +
                             " with baseURI of " + parent.getDocumentBaseURI());
                    
                    definitionPaths.add(i.getLocationURI());
                    
                    try
                    {
                        String baseURI = parent.getDocumentBaseURI();
                        String resolvedLocation;
                        if (baseURI == null)
                            resolvedLocation = new URI(i.getLocationURI()).toString();
                        else
                            resolvedLocation = new URI(parent.getDocumentBaseURI()).resolve(i.getLocationURI()).toString();
                        
                        types.put(resolvedLocation, iDef.getTypes());
                    }
                    catch (URISyntaxException e)
                    {
                        throw new XFireRuntimeException("Couldn't resolve location " + i.getLocationURI(), e);
                    }
                    
                    definitions.add(iDef);
                    portTypes.addAll(iDef.getPortTypes().values());
                    wsdlServices.addAll(iDef.getServices().values());
                    
                    processImports(iDef);
                }
            }
        }
    }

    private Map getPortTypeToPortMap(javax.wsdl.Service wservice)
    {
        Map pt2port = new HashMap();
        
        for (Iterator itr = portTypes.iterator(); itr.hasNext();)
        {
            PortType pt = (PortType) itr.next();
            List ports = new ArrayList();
            pt2port.put(pt, ports);
            
            for (Iterator pitr = wservice.getPorts().values().iterator(); pitr.hasNext();)
            {
                Port port = (Port) pitr.next();
                
                if (port.getBinding().getPortType().equals(pt)) 
                {
                    ports.add(port);
                }
            }
        }
        
        return pt2port;
    }

    /**
     * Gets a Map of Services. The key is the service name and the value is a list
     * of services with that name.
     * @return
     */
    public Map getServices()
    {
        return xFireServices;
    }
    
    /**
     * Gets a list of every service created.
     * @return
     */
    public List getAllServices()
    {
        return allServices;
    }
    
    protected void visit(String location, Types types)
    {
        if (types == null) return;
        
        int schemaCount = 1;
        for (Iterator itr = types.getExtensibilityElements().iterator(); itr.hasNext();)
        {
            ExtensibilityElement ee = (ExtensibilityElement) itr.next();
            
            Element el = null;
            if (ee instanceof UnknownExtensibilityElement)
            {
                UnknownExtensibilityElement uee = (UnknownExtensibilityElement) ee;
                el = uee.getElement();
            }
            else
            {
                // if we are using wsdl4j >= 1.5.1, a specific extensibility
            	// element is defined for schemas, so try retrieve the element
            	try 
            	{
            		Method mth = ee.getClass().getMethod("getElement", new Class[0]);
            		Object val = mth.invoke(ee, new Object[0]);
                    el = (Element) val;
            	} 
                catch (Exception e) {e.printStackTrace();}
            }

            String schemaSystemId = location + "#types?schema"+ schemaCount++;
            schemas.setBaseUri(definition.getDocumentBaseURI());
            XmlSchema schema = schemas.read(el, schemaSystemId);
            
            SchemaInfo schemaInfo = new SchemaInfo();
            schemaInfo.setDefinition(definition);
            schemaInfo.setSchema(schema);
            schemaInfo.setSchemaElement(el);
            
            if (systemId != null && !systemId.equals(location))
                schemaInfo.setImported(true);
            
            schemaInfos.add(schemaInfo);
            schemaCount++;
        }
    }
    
    protected void visit(PortType portType)
    {
        ServiceInfo serviceInfo = new ServiceInfo(null, Object.class);
        portType2serviceInfo.put(portType, serviceInfo);
        serviceInfo.setPortType(portType.getQName());
        Element documentation = portType.getDocumentationElement();
        if( documentation != null ){
            String docText = documentation.getNodeValue() ;//TextContent();
            serviceInfo.setDocumentation(docText);
        }
        if (forceBare) {
			isWrapped = false;
		} else {
			isWrapped = true;
			Iterator itr = portType.getOperations().iterator();
			while (isWrapped && itr.hasNext()) {
				Operation o = (Operation) itr.next();
				isWrapped = isWrapped(o, schemas);
			}
		} 
        
        serviceInfo.setWrapped(isWrapped);
        
        List operations = portType.getOperations();
        for (int i = 0; i < operations.size(); i++)
        {
            Operation operation = (Operation) operations.get(i);
            visit(operation);
            {
                Input input = operation.getInput();
                visit(input);
            }
            {
                Output output = operation.getOutput();
                if (output != null)
                    visit(output);
            }
            
            Collection faults = operation.getFaults().values();
            for (Iterator iterator2 = faults.iterator(); iterator2.hasNext();)
            {
                Fault fault = (Fault) iterator2.next();
                visit(fault);
            }
        }
    }
    
    protected ServiceInfo getServiceInfo(PortType portType)
    {
        return (ServiceInfo) portType2serviceInfo.get(portType);
    }

    protected void visit(Fault fault)
    {
        FaultInfo faultInfo = opInfo.addFault(fault.getName());
        faultInfo.setMessageName(fault.getMessage().getQName());
        if(fault.getDocumentationElement()!= null ){
            faultInfo.setDocumentation(fault.getDocumentationElement().getNodeValue());//TextContent());    
        }
        
        wfault2msg.put(fault, faultInfo);
        
        createMessageParts(faultInfo, fault.getMessage());
    }

    protected void visit(Input input)
    {
        if (isWrapped)
        {
            Part part = (Part) input.getMessage().getParts().values().iterator().next();
            MessageInfo info = opInfo.createMessage(new QName(part.getElementName().getNamespaceURI(),
                                                              input.getMessage().getQName().getLocalPart()));
            winput2msg.put(input, info);
            
            opInfo.setInputMessage(info);
            
            createMessageParts(info, getWrappedSchema(input.getMessage()));
        }
        else
        {
            MessageInfo info = opInfo.createMessage(input.getMessage().getQName());
            winput2msg.put(input, info);
            
            opInfo.setInputMessage(info);
            createMessageParts(info,  input.getMessage());
        }
    }

    protected void visit(Operation operation)
    {
        opInfo = getServiceInfo(portType).addOperation(operation.getName(), null);
        Element docElem = operation.getDocumentationElement();
        if (docElem != null)
        {
           String docText = docElem.getNodeValue();//	TextContent();
           opInfo.setDocumenation(docText);
        }
        wop2op.put(operation, opInfo);
    }

    private void createMessageParts(MessageInfo info, XmlSchemaElement el)
    {
    	if (el == null) 
    	{
    		return;
    	}
    	
    	XmlSchemaComplexType type = (XmlSchemaComplexType) el.getSchemaType();
        if (type == null) 
        {
            return;
        }
      
        if (type.getParticle() instanceof XmlSchemaSequence)
        {
            XmlSchemaSequence seq = (XmlSchemaSequence) type.getParticle();

            XmlSchemaObjectCollection col = seq.getItems();
            for (Iterator itr = col.getIterator(); itr.hasNext();)
            {
                XmlSchemaObject schemaObj = (XmlSchemaObject) itr.next();
                
                if (schemaObj instanceof XmlSchemaElement)
                {
                    createMessagePart(info, (XmlSchemaElement) schemaObj, el.getQName());
                }
            }
        }
    }

    private void createMessagePart(MessageInfo info, XmlSchemaElement element, QName type)
    {
        int index = info.size();
        boolean globalElement = element.getRefName() != null;
        QName name;
        QName schemaType;
        if (globalElement) 
        {
            name = element.getRefName();
            schemaType = name;
        }
        else
        {
            name = element.getQName();
            schemaType = element.getSchemaTypeName();
        }

        MessagePartInfo part = info.addMessagePart(name, XmlSchemaElement.class);
        part.setIndex(index);
        part.setSchemaElement(globalElement);
        part.setWrappedType(type);
        
        SchemaType st = getBindingProvider().getSchemaType(schemaType, service);
        part.setSchemaType(st);
    }

    /**
     * A message is wrapped IFF:
     * 
     * The input message has a single part. 
     * The part is an element. 
     * The element has the same name as the operation. 
     * The element's complex type has no attributes.
     * 
     * @return
     */
    public static boolean isWrapped(Operation op, XmlSchemaCollection schemas)
    {
        Input input = op.getInput();
        Output output = op.getOutput();
        boolean hasOutput = output != null && output.getMessage().getParts() != null;
        
        if (input.getMessage().getParts().size() != 1 || 
            (hasOutput && output.getMessage().getParts().size() != 1)) 
            return false;
        
        Part inPart = (Part) input.getMessage().getParts().values().iterator().next();
        Part outPart = null;
        if (hasOutput)
        	outPart = (Part) output.getMessage().getParts().values().iterator().next();
        
        QName inElementName = inPart.getElementName();
        QName outElementName = null;
        if (hasOutput) outElementName = outPart.getElementName();
        
        if (inElementName == null || (hasOutput && outElementName == null)) 
            return false;
        
        if (!inElementName.getLocalPart().equals(op.getName()) || 
                (hasOutput && !outElementName.getLocalPart().equals(op.getName() + "Response"))) 
            return false;
        
        XmlSchemaElement reqSchemaEl = schemas.getElementByQName(inElementName);
        XmlSchemaElement resSchemaEl = null;
        if (hasOutput) resSchemaEl = schemas.getElementByQName(outElementName);

        if (reqSchemaEl == null) 
            throw new XFireRuntimeException("Couldn't find schema part: " + inElementName);

        if (hasOutput && resSchemaEl == null) 
            throw new XFireRuntimeException("Couldn't find schema part: " + outElementName);

        // Now lets see if we have any attributes...
        // This should probably look at the restricted and substitute types too.
        if (reqSchemaEl.getSchemaType() instanceof XmlSchemaComplexType)
        {
            XmlSchemaComplexType ct = (XmlSchemaComplexType) reqSchemaEl.getSchemaType();
            if (hasAttributes(ct) || ct.getContentModel() != null)
                return false;
            
            // only do a wrapped operation with sequences and all
            if (ct.getParticle() != null 
            		&& !(ct.getParticle() instanceof XmlSchemaSequence) 
            				&& !(ct.getParticle() instanceof XmlSchemaAll))
            	return false;
            
            if (containsAnonymousTypes(ct)) return false;
        } 
        else if (reqSchemaEl.getSchemaType() != null) 
        {
            return false;
        }
        
        if (hasOutput && resSchemaEl.getSchemaType() instanceof XmlSchemaComplexType)
        {
            XmlSchemaComplexType ct = (XmlSchemaComplexType) resSchemaEl.getSchemaType();
            if (hasAttributes(ct))
                return false;

            if (ct.getContentModel() != null)
                return false;
            
            if (ct.getParticle() != null 
            		&& !(ct.getParticle() instanceof XmlSchemaSequence) 
            				&& !(ct.getParticle() instanceof XmlSchemaAll))
                return false;
            
            if (containsAnonymousTypes(ct)) return false;

        }
        else if (hasOutput && resSchemaEl.getSchemaType() != null) 
        {
            return false;
        }
        
        return true;
    }

	private static boolean containsAnonymousTypes(XmlSchemaComplexType ct) {
		XmlSchemaGroupBase particle = (XmlSchemaGroupBase)ct.getParticle();
		if (particle == null) {
			return false;
		}
		
		XmlSchemaObjectCollection items = particle.getItems();
		for (int i = 0; i < items.getCount(); i++) {
			XmlSchemaObject item = items.getItem(i);
			if (item instanceof XmlSchemaElement) {
				XmlSchemaElement el = (XmlSchemaElement) item;
				
				if (el.getSchemaTypeName() == null) return true;
			} else if (item instanceof XmlSchemaElement) {
				XmlSchemaComplexType el = (XmlSchemaComplexType) item;
				
				if (el.getParticle() != null) return true;
			}
		}
		return false;
	}
    
    private XmlSchemaElement getWrappedSchema(Message message)
    {
        Part part = (Part) message.getParts().values().iterator().next();
        
        XmlSchemaElement schemaEl = schemas.getElementByQName(part.getElementName());
        
        if (schemaEl.getSchemaType() instanceof XmlSchemaComplexType)
            return schemaEl;
        
        return null;
    }
    
    protected static boolean hasAttributes(XmlSchemaComplexType complexType)
    {
        // Now lets see if we have any attributes...
        // This should probably look at the restricted and substitute types too.
        
        if (complexType.getAnyAttribute() != null ||
                complexType.getAttributes().getCount() > 0)
            return true;
        else
            return false;
    }
    
    private void createMessageParts(MessagePartContainer info, Message msg)
    {
        List parts = msg.getOrderedParts(null);
        
        for (Iterator itr = parts.iterator(); itr.hasNext();)
        {
            Part entry = (Part) itr.next();
            
            // We're extending an abstract schema type
            QName typeName = entry.getTypeName();
            if (typeName != null)
            {
                QName partName = new QName(getTargetNamespace(), entry.getName());
                
                MessagePartInfo part = info.addMessagePart(partName, null);
                part.setSchemaElement(false);
                part.setSchemaType(getBindingProvider().getSchemaType(typeName, service));
                part.setIndex(info.size()-1);
            }

            // We've got a concrete schema type
            QName elementName = entry.getElementName();
            if (elementName != null)
            {
                MessagePartInfo part = info.addMessagePart(elementName, null);
                part.setSchemaType(getBindingProvider().getSchemaType(typeName, service));
                part.setIndex(info.size()-1);
                if( entry.getDocumentationElement()!= null ){
                    part.setDocumentation(entry.getDocumentationElement().getNodeValue());//TextContent());
                }
            }
        }
    }

    protected String getTargetNamespace()
    {
        return getDefinition().getTargetNamespace();
    }

    protected void visit(Output output)
    {
        MessageInfo info = opInfo.createMessage(
            new QName(opInfo.getInputMessage().getName().getNamespaceURI(),
                      output.getMessage().getQName().getLocalPart()));
        opInfo.setOutputMessage(info);
        woutput2msg.put(output, info);
        
        if (isWrapped)
        {
            createMessageParts(info, getWrappedSchema(output.getMessage()));
        }
        else
        {
            createMessageParts(info, output.getMessage());
        }
    }

    /**
     * Returns a Collection of SchemaInfo objects.
     * @return
     */
    public List getSchemas()
    {
        return schemaInfos;
    }
    
    public XmlSchemaCollection getSchemaCollection()
    {
        return schemas;
    }

    public boolean isForceBare() {
        return forceBare;
    }

    public void setForceBare(boolean forceBare) {
        this.forceBare = forceBare;
    }
}
