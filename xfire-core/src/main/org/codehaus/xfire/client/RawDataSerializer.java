package org.codehaus.xfire.client;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.STAXUtils;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class RawDataSerializer implements MessageSerializer {

	private final XMLStreamReader reader;

	public RawDataSerializer(XMLStreamReader reader) {
		this.reader = reader;
	}

	public void readMessage(InMessage message, MessageContext context)
			throws XFireFault {

	}

	/* (non-Javadoc)
	 * @see org.codehaus.xfire.exchange.MessageSerializer#writeMessage(org.codehaus.xfire.exchange.OutMessage, javax.xml.stream.XMLStreamWriter, org.codehaus.xfire.MessageContext)
	 */
	public void writeMessage(OutMessage message, XMLStreamWriter writer,
			MessageContext context) throws XFireFault {

		try {
			STAXUtils.copy(reader, writer);
		} catch (XMLStreamException e) {
			XFireFault.createFault(e);
		}
	}

}
