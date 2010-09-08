package org.codehaus.xfire.addressing;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class AddressingInData
{

    private AddressingHeaders outHeaders;

    private AddressingHeaders inHeaders;

    public AddressingHeaders getOutHeaders()
    {
        return outHeaders;
    }

    public void setOutHeaders(AddressingHeaders headers)
    {
        this.outHeaders = headers;
    }

    public AddressingHeaders getInHeaders()
    {
        return inHeaders;
    }

    public void setInHeaders(AddressingHeaders inHeaders)
    {
        this.inHeaders = inHeaders;
    }

}
