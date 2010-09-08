package org.codehaus.xfire.exchange;


public class OutMessage
    extends AbstractMessage
{
    private MessageSerializer serializer;
    
    public OutMessage(String uri)
    {
        setUri(uri);
    }

    public MessageSerializer getSerializer()
    {
        return serializer;
    }

    public void setSerializer(MessageSerializer serializer)
    {
        this.serializer = serializer;
    }
}
