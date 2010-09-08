package org.codehaus.xfire.util.stax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class DOMStreamWriter implements XMLStreamWriter
{

    public void close()
        throws XMLStreamException
    {
    }

    public void flush()
        throws XMLStreamException
    {
    }
}
