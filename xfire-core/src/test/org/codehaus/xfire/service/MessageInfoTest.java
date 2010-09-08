package org.codehaus.xfire.service;


import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class MessageInfoTest
        extends TestCase
{
    private MessageInfo message;

    protected void setUp()
            throws Exception
    {
        ServiceInfo service = new ServiceInfo(new QName("serviceport"), getClass());
        Method echoMethod = getClass().getMethod("method", new Class[0]);
        OperationInfo operation = service.addOperation("operation", echoMethod);
        message = new MessageInfo(new QName("name"), operation);
    }

    public void method()
    {
    }

    public void testOrdering()
            throws Exception
    {
        MessagePartInfo part1 = message.addMessagePart(new QName("part1"), String.class);
        MessagePartInfo part2 = message.addMessagePart(new QName("part2"), String.class);
        MessagePartInfo part3 = message.addMessagePart(new QName("part3"), String.class);

        assertEquals(part1, message.getMessageParts().get(0));
        assertEquals(part2, message.getMessageParts().get(1));
        assertEquals(part3, message.getMessageParts().get(2));

        message.removeMessagePart(part2.getName());

        assertEquals(part1, message.getMessageParts().get(0));
        assertEquals(part3, message.getMessageParts().get(1));

    }
}