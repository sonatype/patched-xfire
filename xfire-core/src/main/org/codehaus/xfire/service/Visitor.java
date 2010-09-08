package org.codehaus.xfire.service;



/**
 * Defines the contract for classes that iterate over the <code>*Info</code> classes. Used to recurse into {@link
 * ServiceInfo}, {@link OperationInfo}, {@link MessageInfo}, etc.
 * <p/>
 * <strong>Note</strong> that implementations of this interface are not required to recurse themselves; instead, this is
 * handled by the various vistable implementations.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see Visitable
 */
public interface Visitor
{
    /**
     * Receive notification at the beginning of a endpoint visit.
     *
     * @param endpoint the service endpoint.
     */
    void startEndpoint(Service endpoint);

    /**
     * Receive notatification of the end of a endpoint visit.
     *
     * @param endpoint
     */
    void endEndpoint(Service endpoint);

    /**
     * Receive notification at the beginning of a service visit.
     *
     * @param serviceInfo the service.
     */
    void startService(ServiceInfo serviceInfo);

    /**
     * Receive notatification of the end of a service visit.
     *
     * @param serviceInfo
     */
    void endService(ServiceInfo serviceInfo);

    /**
     * Receive notification at the beginning of a operation visit.
     *
     * @param operationInfo the operation.
     */
    void startOperation(OperationInfo operationInfo);

    /**
     * Receive notification at the end of a operation visit.
     *
     * @param operationInfo the operation.
     */
    void endOperation(OperationInfo operationInfo);

    /**
     * Receive notification at the beginning of a message visit.
     *
     * @param messageInfo the message.
     */
    void startMessage(MessageInfo messageInfo);

    /**
     * Receive notification at the end of a message visit.
     *
     * @param messageInfo the message.
     */
    void endMessage(MessageInfo messageInfo);

    /**
     * Receive notification at the beginning of a fault visit.
     *
     * @param faultInfo the fault.
     */
    void startFault(FaultInfo faultInfo);

    /**
     * Receive notification at the end of a fault visit.
     *
     * @param faultInfo the fault.
     */
    void endFault(FaultInfo faultInfo);

    /**
     * Receive notification at the beginning of a message part visit.
     *
     * @param messagePartInfo the message part info.
     */
    void startMessagePart(MessagePartInfo messagePartInfo);

    /**
     * Receive notification at the end of a message part visit.
     *
     * @param messagePartInfo the message part info.
     */
    void endMessagePart(MessagePartInfo messagePartInfo);
}
