package org.codehaus.xfire.service;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.AbstractContext;

public class Extensible extends AbstractContext
{
    private List extensions = new ArrayList();
    
    public List getExtensions()
    {
        return extensions;
    }

    public void addExtension(Object ext)
    {
        extensions.add(ext);
    }
}
