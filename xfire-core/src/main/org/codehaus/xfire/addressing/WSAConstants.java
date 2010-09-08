package org.codehaus.xfire.addressing;

/**
 * Constants for WS-Addressing.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WSAConstants
{
    String WSA_NAMESPACE_200408 = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    String WSA_NAMESPACE_200508 = "http://www.w3.org/2005/08/addressing";
    String WSA_200508_ANONYMOUS_URI = "http://www.w3.org/2005/08/addressing/anonymous";
    String WSA_200508_NONE_URI ="http://www.w3.org/2005/08/addressing/none";
    
    String WSA_200408_ANONYMOUS_URI = "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous";

    String WSA_200508_FAULT_ACTION="http://www.w3.org/2005/08/addressing/fault";
    
    String WSA_PREFIX = "wsa";
    
    String WSA_ACTION = "Action";

    String WSA_TO = "To";

    String WSA_FAULT_TO = "FaultTo";

    String WSA_FROM = "From";

    String WSA_REPLY_TO = "ReplyTo";

    String WSA_RELATES_TO = "RelatesTo";

    String WSA_MESSAGE_ID = "MessageID";

    String WSA_RELATIONSHIP_TYPE = "RelationshipType";

    String WSA_REFERENCE_PROPERTIES = "ReferenceProperties";

    String WSA_REFERENCE_PARAMETERS = "ReferenceParameters";

    String WSA_ADDRESS = "Address";

    String WSA_INTERFACE_NAME = "InterfaceName";

    String WSA_SERVICE_NAME = "ServiceName";

    String WSA_ENDPOINT_NAME = "EndpointName";

    String WSA_POLICIES = "Policies";
    
    String WSA_METADATA = "Metadata";

    String WSA_NAMESPACE = WSA_NAMESPACE_200508;
    
    String WSA_IS_REF_PARAMETER="isReferenceParameter";
}
