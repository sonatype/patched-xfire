package org.codehaus.xfire.attachments;

import java.awt.Image;


/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class DefaultDataContentHandlerFactory
    extends AbstractDataContentHandlerFactory
{
    private static ImageDataContentHandler imgHandler = new ImageDataContentHandler();

    public DefaultDataContentHandlerFactory()
    {
        register("image/jpeg", Image.class, imgHandler);
        register("image/tiff", Image.class, imgHandler);
    }
}
