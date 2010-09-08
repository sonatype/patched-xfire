package org.codehaus.xfire.wsdl11.parser;

import java.io.IOException;

import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.util.Resolver;
import org.xml.sax.InputSource;

/**
 * Resolves URIs in a more sophisticated fashion than XmlSchema's default URI
 * Resolver does by using our own {@link org.apache.cxf.resource.URIResolver}
 * class.
 */
public class XmlSchemaURIResolver extends DefaultURIResolver {

	public XmlSchemaURIResolver() {
	}

	public InputSource resolveEntity(String targetNamespace,
			String schemaLocation, String baseUri) {
		int idx = schemaLocation.indexOf('#');
		if (idx != -1) {
			schemaLocation = schemaLocation.substring(0, idx);
		}
		
		idx = baseUri.indexOf('#');
		if (idx != -1) {
			baseUri = baseUri.substring(0, idx);
		}
		
		
		return super.resolveEntity(targetNamespace, schemaLocation, baseUri);
	}
}
