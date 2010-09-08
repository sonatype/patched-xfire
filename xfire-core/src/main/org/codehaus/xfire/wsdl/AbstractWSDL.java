package org.codehaus.xfire.wsdl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.WSDLException;
import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;


/**
 * Provides schema functionality for a WSDLBuilder.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractWSDL
    implements WSDLWriter
{
    private static final StaxBuilder builder = new StaxBuilder();

    private Service service;

    private Map dependencies = new HashMap();
    
    private Map namespaceImports = new HashMap();
    
    private Element schemaTypes;

    protected Map typeMap = new HashMap();
    
    private boolean schemaLocationRemoved = true;
    
    /*-------------------------------------------------
     * Namespace and QName definitions for easy access.
     *-------------------------------------------------*/

    public final static Namespace XSD_NS = Namespace.getNamespace(SoapConstants.XSD_PREFIX, 
                                                                  SoapConstants.XSD);

    public final static String GENERATE_IMPORTS = "wsdlBuilder.generateImports";
    public final static String CLEAN_IMPORTS = "wsdlBuilder.cleanImports";
    public static final String REMOVE_ALL_IMPORTS = "wsdlBuilder.removeAllImports";
    
    private boolean generateImports = false;
    private boolean cleanImports = true;
    private boolean removeAllImports = false;
    
    public AbstractWSDL(Service service) throws WSDLException
    {
        this.service = service;

        Element root = new Element("types", "wsdl", WSDL11_NS);
        setSchemaTypes(root);
        root.addNamespaceDeclaration(Namespace.getNamespace(SoapConstants.XSD_PREFIX, SoapConstants.XSD));
    }


    protected void initialize()
    {
        addNamespace("soap11", Soap11.getInstance().getNamespace());
        addNamespace("soapenc11", Soap11.getInstance().getSoapEncodingStyle());
        addNamespace("soap12", Soap12.getInstance().getNamespace());
        addNamespace("soapenc12", Soap12.getInstance().getSoapEncodingStyle());
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("wsdl", WSDL11_NS);
        addNamespace("wsdlsoap", WSDL11_SOAP_NS);
        addNamespace("tns", getTargetNamespace());
        
        // If port type namespace if different from target namespace,
        // we must add its definition
        if (!getTargetNamespace().equals(service.getServiceInfo().getPortType().getNamespaceURI()))
        {
            addNamespace("itf", service.getServiceInfo().getPortType().getNamespaceURI());
        }

        generateImports = Boolean.valueOf((String) service.getProperty(GENERATE_IMPORTS)).booleanValue();
        removeAllImports = Boolean.valueOf((String) service.getProperty(REMOVE_ALL_IMPORTS)).booleanValue();

        String cleanImpProp = (String) service.getProperty(CLEAN_IMPORTS);
        if (cleanImpProp != null) cleanImports = Boolean.valueOf(cleanImpProp).booleanValue();
        
        List schemas = (List) service.getProperty(ObjectServiceFactory.SCHEMAS);
        if (schemas != null) addSchemas(schemas);
    }
    
    protected void updateImports()
        throws WSDLException
    {
        if (removeAllImports)
        {
            removeAllImports();
        }
        else
        {
            if (generateImports) writeImports();
            
            if (cleanImports) cleanImports();
        }        
    }

    /**
     * Write xs:import elements for each schema.
     */
    protected void writeImports()
    {
        for (Iterator itr = namespaceImports.entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();
            
            String uri = (String) entry.getKey();
            Set imports = (Set) entry.getValue();
            
            Element schema = createSchemaType(uri);
            
            for (Iterator importItr = imports.iterator(); importItr.hasNext();)
            {
                String ns = (String) importItr.next();
                if (!ns.equals(SoapConstants.XSD) && getImport(schema, ns) == null)
                {
                    Element importEl = new Element("import", XSD_NS);
                    importEl.setAttribute(new Attribute("namespace", ns));
                    
                    schema.addContent(0, importEl);
                }
            }
        }
    }

    public Element getImport(Element schema, String ns)
    {
        List children = schema.getChildren("import", Namespace.getNamespace(SoapConstants.XSD));
        
        for (int i = 0; i < children.size(); i++)
        {
            Element importEl = (Element) children.get(i);
            String value = importEl.getAttributeValue("namespace");
            
            if (value != null && value.equals(ns)) return importEl;
        }
        
        return null;
    }

    /**
     * Removes imports from all the schemas.
     */
    protected void removeAllImports()
    {
        for (Iterator itr = schemaTypes.getChildren().iterator(); itr.hasNext();)
        {
            Element schema = (Element) itr.next();
            removeImports(schema);
        }
    }


    protected void removeImports(Element schema)
    {
        List children = schema.getChildren("import", Namespace.getNamespace(SoapConstants.XSD));
        
        for (Iterator sitr = children.iterator(); sitr.hasNext();)
        {
            sitr.next();
            sitr.remove();
        }
    }
    
    protected void cleanImports()
    {
        for (Iterator itr = schemaTypes.getChildren().iterator(); itr.hasNext();)
        {
            Element schema = (Element) itr.next();
            List children = schema.getChildren("import", Namespace.getNamespace(SoapConstants.XSD));
            
            for (Iterator sitr = children.iterator(); sitr.hasNext();)
            {
                cleanImport((Element) sitr.next());
            }
        }
    }
    
    public void cleanImport(Element node)
    {
        Attribute schemaLoc = node.getAttribute("schemaLocation");
        
        // TODO: How do we make sure this is imported???
        
        if (schemaLoc != null)
            schemaLoc.detach();
    }
    
    protected abstract void writeComplexTypes();

    public void addDependency(SchemaType type)
    {
        if (!type.isComplex())
        {
            return;
        }
        
        if (!hasDependency(type))
        {
            dependencies.put(type.getSchemaType(), type);

            Element e = createSchemaType(type.getSchemaType().getNamespaceURI());
            type.writeSchema(e);
            
            Set deps = type.getDependencies();

            if (deps != null)
            {
                for (Iterator itr = deps.iterator(); itr.hasNext();)
                {
                    SchemaType child = (SchemaType) itr.next();
                    addDependency(child);
                    addNamespaceImport(type.getSchemaType().getNamespaceURI(), 
                                       child.getSchemaType().getNamespaceURI());
                }
            }
        }
    }

    protected boolean hasDependency(SchemaType type)
    {
        return dependencies.containsKey(type.getSchemaType());
    }

    /**
     * Adds an import to another namespace. 
     * @param uri The namespace to import into.
     * @param imported The namespace to import.
     */
    public void addNamespaceImport(String uri, String imported)
    {
        if (uri.equals(imported)) return;
        
        Set imports = (Set) namespaceImports.get(uri);
        
        if (imports == null)
        {
            imports = new HashSet();
            namespaceImports.put(uri, imports);
        }
        
        imports.add(imported);
    }

    public abstract void write(OutputStream out)
        throws IOException;

    public void addNamespace(String prefix, String uri)
    {
        Namespace declaredUri = schemaTypes.getNamespace(prefix);
        if (declaredUri == null)
        {
            schemaTypes.addNamespaceDeclaration(Namespace.getNamespace(prefix, uri));
        }
        else if (!declaredUri.getURI().equals(uri))
        {
            throw new XFireRuntimeException("Namespace conflict: " + declaredUri
                    + " was declared but " + uri + " was attempted.");
        }
    }

    public String getNamespacePrefix(String uri)
    {
        return NamespaceHelper.getUniquePrefix(schemaTypes, uri);
    }

    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    public String getTargetNamespace()
    {
        return service.getTargetNamespace();
    }

    public void addSchemas(List schemaLocations)
    {
        for (Iterator itr = schemaLocations.iterator(); itr.hasNext();)
        {
            addSchema((String) itr.next());
        }
    }
    
    /**
     * Loads a schema off the filesystem or the classpath and adds it to the WSDL types section.
     * 
     * @param location
     */
    public void addSchema(String location)
    {
        // Try loading the file as a file, then on the classpath
        InputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(location);
        } 
        catch (FileNotFoundException e)
        {
            fileInputStream = ClassLoaderUtils.getResourceAsStream(location, getClass());
        }
        
        if (fileInputStream == null)
            throw new XFireRuntimeException("Couldnt load schema file: " + location);
        
        // Load in the schema
        Document schema = null;
        try
        {
            schema = builder.build(fileInputStream);
        } 
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Error parsing schema file: " + location, e);
        }
        
        // Remove the schemaLocation elements
        if (isSchemaLocationRemoved())
            cleanImports(schema);
        
        String targetNamespace = schema.getRootElement().getAttributeValue("targetNamespace");
        if (targetNamespace != null)
        {
            Element root = schema.getRootElement();
            root.detach();
            setSchema(targetNamespace, root);
        }
        else
        {
            throw new XFireRuntimeException("Could not find target namespace in schema: " + location);
        }
    }

    public boolean isSchemaLocationRemoved()
    {
        return schemaLocationRemoved;
    }

    public void setSchemaLocationRemoved(boolean schemaLocationRemoved)
    {
        this.schemaLocationRemoved = schemaLocationRemoved;
    }

    /**
     * Removes the schemaLocation attribute from an &lt;xsd:import&gt; statement.
     * @param schema
     */
    protected void cleanImports(Document schema)
    {
        List nodes = getMatches(schema, "//xsd:import");
        for (int i = 0; i < nodes.size(); i++)
        {
            Element imp = (Element) nodes.get(i);

            Attribute loc = imp.getAttribute("schemaLocation");

            if (loc != null)
            {
                loc.detach();
            }
        }
    }
    
    private List getMatches(Object doc, String xpath)
    {
        try
        {
            XPath path = XPath.newInstance(xpath);
            path.addNamespace("xsd", SoapConstants.XSD);
            path.addNamespace("s", SoapConstants.XSD);
            List result = path.selectNodes(doc);
            return result;
        }
        catch (JDOMException e)
        {
            throw new XFireRuntimeException("Error evaluating xpath " + xpath, e);
        }
    }
    
    /**
     * Create a shcema type element and store it to be written later on.
     * 
     * @param namespace
     *            The namespace to create the type in.
     * @return
     */
    public Element createSchemaType(String namespace)
    {
        Element e = (Element) typeMap.get(namespace);

        if (e == null)
        {
            e = new Element("schema", XSD_NS);

            e.setAttribute(new Attribute("targetNamespace", namespace));
            e.setAttribute(new Attribute("elementFormDefault", "qualified"));
            e.setAttribute(new Attribute("attributeFormDefault", "qualified"));

            setSchema(namespace, e);
        }

        return e;
    }

    protected boolean hasSchema(String namespace)
    {
        return typeMap.containsKey(namespace);
    }
    
    protected void setSchema(String namespace, Element schema)
    {
        typeMap.put(namespace, schema);
        getSchemaTypes().addContent(schema);
    }

    protected Element getSchemaTypes()
    {
        return schemaTypes;
    }

    protected void setSchemaTypes(Element schemaTypes)
    {
        this.schemaTypes = schemaTypes;
    }
}
