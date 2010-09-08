package org.codehaus.xfire.exchange;

import javax.xml.stream.XMLStreamReader;

/**
 * A "in" message. These arrive at endpoints.

 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class InMessage
    extends AbstractMessage
{
    private XMLStreamReader xmlStreamReader;

    public InMessage()
    {
    }
    
    public InMessage(XMLStreamReader xmlStreamReader)
    {
        this(xmlStreamReader, ANONYMOUS_URI);
    }
    
    public InMessage(XMLStreamReader xmlStreamReader, String uri)
    {
        this.xmlStreamReader = xmlStreamReader;
        setUri(uri);
        setEncoding(xmlStreamReader.getCharacterEncodingScheme());
    }

    public void setXMLStreamReader(XMLStreamReader xmlStreamReader)
    {
        this.xmlStreamReader = xmlStreamReader;
    }

    public XMLStreamReader getXMLStreamReader()
    {
        return xmlStreamReader;
    }
}