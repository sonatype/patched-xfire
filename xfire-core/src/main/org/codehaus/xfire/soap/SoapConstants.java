package org.codehaus.xfire.soap;


/**
 * SOAP constants from the specs.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public class SoapConstants
{
    /** Document styles. */

    /**
     * Constant used to specify a rpc binding style.
     */
    public static final String STYLE_RPC = "rpc";

    /**
     * Constant used to specify a document binding style.
     */
    public static final String STYLE_DOCUMENT = "document";

    /**
     * Constant used to specify a wrapped binding style.
     */
    public static final String STYLE_WRAPPED = "wrapped";

    /**
     * Constant used to specify a message binding style.
     */
    public static final String STYLE_MESSAGE = "message";

    /**
     * Constant used to specify a literal binding use.
     */
    public static final String USE_LITERAL = "literal";

    /**
     * Constant used to specify a encoded binding use.
     */
    public static final String USE_ENCODED = "encoded";

    /**
     * XML Schema Namespace.
     */
    public static final String XSD = "http://www.w3.org/2001/XMLSchema";
    public static final String XSD_PREFIX = "xsd";

    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XSI_PREFIX = "xsi";

    public static final String MEP_ROBUST_IN_OUT = "urn:xfire:mep:in-out";
    public static final String MEP_IN = "urn:xfire:mep:in";
    
    public static final String SOAP_ACTION = "SOAPAction";
    
    /**
     * Whether or not MTOM should be enabled for each service.
     */
    public static final String MTOM_ENABLED = "mtom-enabled";

    
}
