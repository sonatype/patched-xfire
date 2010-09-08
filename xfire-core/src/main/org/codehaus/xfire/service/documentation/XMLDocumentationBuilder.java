package org.codehaus.xfire.service.documentation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Builds DocumentationProvider based on XML files.
 *
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 *
 */
public class XMLDocumentationBuilder { 

	protected static final Log log = LogFactory.getLog(XMLDocumentationBuilder.class.getName());

	public static final String DOCUMENTATION_TAG = "documentation";

	public static final String METHOD_TAG = "method";

	public static final String PARAMTER_TAG = "parameter";

	public static final String RETURN_TAG = "return";

	public static final String EXCEPTION_TAG = "exception";

	public static final String NAME_ATTR = "name";

	public static final String INDEX_ATTR = "index";

	public static final String CLASS_ATTR = "class";

	public static final String ARGUMENTS_NUMBER_ATTR = "parametersNumber";

	public static final String CONFIG_SUFIX = ".doc.xml";

	/**
	 * @param service
	 * @return
	 */
	public DocumentationProvider build(ServiceInfo service) {

		Document doc = loadDocument(service);

		return (doc == null ? null : parseDocument(doc));
	}

	/**
	 * @param doc
	 * @return
	 */
	protected DocumentationProvider parseDocument(Document doc) {

		DocumentationProvider docProvider = new DocumentationProvider();
		Element service = doc.getRootElement();
		// process service documentation
		String documentation = readDocumentations(service);
		if (documentation != null) {
			docProvider.setServiceDocumentation(documentation);
		}
		// process method info
		List operationsList = service.getChildren(METHOD_TAG);

		for( Iterator opIterator = operationsList.iterator(); opIterator.hasNext(); ){
			Element element = (Element) opIterator.next();

			String name = element.getAttribute(NAME_ATTR).getValue();
			String argNrStr = element.getAttribute(ARGUMENTS_NUMBER_ATTR).getValue();
			String opDocumentation = readDocumentations(element);
			// Create empty parameters list
			int argNr = Integer.parseInt(argNrStr);
			List params = new ArrayList(argNr);
			for (int p = 0; p < argNr; p++) {
				params.add(null);
			}
			// process described paramters
			List parameters = element.getChildren(PARAMTER_TAG);
			for (int p = 0; p < parameters.size(); p++) {

				Element param = (Element) parameters.get(p);

				String indexStr = param.getAttribute(INDEX_ATTR).getValue();
				String paramDoc = readDocumentations(param);
				int paramIdx = Integer.parseInt(indexStr);
				if( paramIdx<0 || paramIdx > argNr){
					throw new RuntimeException("Incorrect parameter index ["+paramIdx+"]. Allowed values are : <0,"+(argNr-1)+">");
				}
				params.set(paramIdx, paramDoc);
			}
			// process result documentation
			String returnDoc = null;
			Element returnElem = element.getChild(RETURN_TAG);
			if (returnElem != null) {
				returnDoc = readDocumentations(returnElem);
			}
			// process exceptions
			List exceptions = element.getChildren(EXCEPTION_TAG);
			Map excMap = new HashMap();
			for (int e = 0; e < exceptions.size(); e++) {
				Element param = (Element) exceptions.get(e);
				String exClass = param.getAttribute(CLASS_ATTR).getValue();
				excMap.put(exClass, readDocumentations(param));
			}

			docProvider.addOperation(name, opDocumentation, params, returnDoc, excMap);
		}

		return docProvider;
	}

	/**
	 * @param elem
	 * @return
	 */
	private String readDocumentations(Element elem) {

		Element documentation = elem.getChild(DOCUMENTATION_TAG);
		if (documentation == null) {
			return null;
		}
		return documentation.getTextTrim();

	}

	/**
	 * @param service
	 * @return
	 */
	protected Document loadDocument(ServiceInfo service) {

		Class clazz = service.getServiceClass();
		// get class name w/o .java suffix
		int idx = clazz.getName().lastIndexOf(".");
		String className = clazz.getName().substring(idx + 1);
		String fileName = className + CONFIG_SUFIX;
		log.debug("Searching for " + fileName + " config..");
		InputStream inStr = clazz.getResourceAsStream(fileName);
		if (inStr == null) {
			log.debug("Config " + fileName + " NOT found.");
			return null;
		}

		StaxBuilder builder = new StaxBuilder();
		try {
			log.debug("Config " + fileName + " found.");
			return builder.build(inStr);
		} catch (XMLStreamException e) {
			throw new XFireRuntimeException(e.getMessage());
		}

	}

}
