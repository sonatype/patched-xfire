package org.codehaus.xfire.handler;

public class Phase
    implements Comparable
{
    public final static String TRANSPORT = "transport";
    public final static String PARSE = "parse";
    public final static String PRE_DISPATCH = "pre-dispatch";
    public final static String DISPATCH = "dispatch";
    public final static String POLICY = "policy";
    public final static String USER = "user";
    public final static String PRE_INVOKE = "pre-invoke";
    public final static String SERVICE = "service";
    public final static String POST_INVOKE = "post-invoke";
    public final static String SEND = "send";
    
    private String name;
    private int priority;
    
    public Phase(String name, int priority)
    {
        this.name = name;
        this.priority = priority;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public int getPriority()
    {
        return priority;
    }
    public void setPriority(int priority)
    {
        this.priority = priority;
    }
    
    public boolean equals(Object obj)
    {
        if (obj.hashCode() != hashCode())
            return false;
        
        if (!(obj instanceof Phase))
            return false;
        
        Phase p = (Phase) obj;
        if (p.getName().equals(name) && p.getPriority() == priority)
        {
            return true;
        }
        
        return false;
    }
    
    public int hashCode()
    {
        return priority ^= name.hashCode();
    }
    
    public int compareTo(Object obj)
    {
        Phase phase = (Phase) obj;
        
        int p2 = phase.getPriority();
        if (p2 < priority) return 1;
        if (p2 > priority) return -1;
        
        return 0;
    }
    
    
}
