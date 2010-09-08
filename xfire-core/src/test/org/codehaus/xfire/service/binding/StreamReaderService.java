package org.codehaus.xfire.service.binding;

import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

public class StreamReaderService extends Assert
{
    public void process(XMLStreamReader reader)
    {
        assertEquals("echo", reader.getLocalName());
    }
}
