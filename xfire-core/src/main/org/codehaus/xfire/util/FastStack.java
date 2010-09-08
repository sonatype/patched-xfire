package org.codehaus.xfire.util;

import java.util.ArrayList;
import java.util.EmptyStackException;

public class FastStack
    extends ArrayList
{
    public void push(Object o)
    {
        add(o);
    }
    
    public Object pop()
    {
        if (empty()) throw new EmptyStackException();
        
        return remove(size()-1);
    }
    
    public boolean empty()
    {
        return size() == 0;
    }

    public Object peek()
    {
        if (empty()) throw new EmptyStackException();
        
        return get(size()-1);
    }
}
