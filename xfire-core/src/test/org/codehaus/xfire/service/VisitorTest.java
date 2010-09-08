package org.codehaus.xfire.service;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class VisitorTest
        extends TestCase
{
    public void method()
    {

    }

    public void testVisitor()
            throws Exception
    {
        ServiceInfo service = new ServiceInfo(new QName("serviceport"), String.class);
        Service endpoint = new Service(service);
        endpoint.setName(new QName("service"));
        Method method = getClass().getMethod("method", new Class[0]);
        OperationInfo operation = service.addOperation("operation", method);
        MessageInfo inputMessage = operation.createMessage(new QName("input"));
        operation.setInputMessage(inputMessage);
        MessageInfo outputMessage = operation.createMessage(new QName("output"));
        operation.setOutputMessage(outputMessage);
        FaultInfo faultInfo = operation.addFault("fault");
        MessagePartInfo partInfo1 = inputMessage.addMessagePart(new QName("part1"), String.class);
        MessagePartInfo partInfo2 = inputMessage.addMessagePart(new QName("part2"), String.class);

        MockVisitor visitor = new MockVisitor();
        endpoint.accept(visitor);

        assertTrue(visitor.started(endpoint));
        assertTrue(visitor.started(service));
        assertTrue(visitor.started(operation));
        assertTrue(visitor.started(inputMessage));
        assertTrue(visitor.started(outputMessage));
        assertTrue(visitor.started(faultInfo));
        assertTrue(visitor.started(partInfo1));
        assertTrue(visitor.started(partInfo2));

        assertTrue(visitor.ended(endpoint));
        assertTrue(visitor.ended(service));
        assertTrue(visitor.ended(operation));
        assertTrue(visitor.ended(inputMessage));
        assertTrue(visitor.ended(outputMessage));
        assertTrue(visitor.ended(faultInfo));
        assertTrue(visitor.ended(partInfo1));
        assertTrue(visitor.ended(partInfo2));
    }

    private class MockVisitor
            implements Visitor
    {
        private List started = new ArrayList();
        private List ended = new ArrayList();

        public void startEndpoint(Service endpoint)
        {
            assertNotNull(endpoint);
            started.add(endpoint);
        }

        public void endEndpoint(Service endpoint)
        {
            assertNotNull(endpoint);
            ended.add(endpoint);
        }

        public void startService(ServiceInfo serviceInfo)
        {
            assertNotNull(serviceInfo);
            started.add(serviceInfo);
        }

        public void endService(ServiceInfo serviceInfo)
        {
            assertNotNull(serviceInfo);
            ended.add(serviceInfo);
        }

        public void startOperation(OperationInfo operationInfo)
        {
            assertNotNull(operationInfo);
            started.add(operationInfo);
        }

        public void endOperation(OperationInfo operationInfo)
        {
            assertNotNull(operationInfo);
            ended.add(operationInfo);
        }

        public void startMessage(MessageInfo messageInfo)
        {
            assertNotNull(messageInfo);
            assertFalse(messageInfo.getClass().equals(FaultInfo.class));
            started.add(messageInfo);
        }

        public void endMessage(MessageInfo messageInfo)
        {
            assertNotNull(messageInfo);
            assertFalse(messageInfo.getClass().equals(FaultInfo.class));
            ended.add(messageInfo);
        }

        public void startFault(FaultInfo faultInfo)
        {
            assertNotNull(faultInfo);
            started.add(faultInfo);
        }

        public void endFault(FaultInfo faultInfo)
        {
            assertNotNull(faultInfo);
            ended.add(faultInfo);
        }

        public void startMessagePart(MessagePartInfo messagePartInfo)
        {
            assertNotNull(messagePartInfo);
            started.add(messagePartInfo);
        }

        public void endMessagePart(MessagePartInfo messagePartInfo)
        {
            assertNotNull(messagePartInfo);
            ended.add(messagePartInfo);
        }

        public boolean started(Visitable visitable)
        {
            return started.contains(visitable);
        }

        public boolean ended(Visitable visitable)
        {
            return ended.contains(visitable);
        }
    }
}