package org.codehaus.xfire.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DeleteOnCloseFileInputStream
    extends FileInputStream
{

    private final File file;

    private boolean delete = true;

    public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException
    {
        super(file);
        this.file = file;
    }

    public void close()
        throws IOException
    {
        super.close();
        
        if (delete)
            file.delete();
    }

    public boolean isDelete()
    {
        return delete;
    }

    public void setDelete(boolean delete)
    {
        this.delete = delete;
    }

}
