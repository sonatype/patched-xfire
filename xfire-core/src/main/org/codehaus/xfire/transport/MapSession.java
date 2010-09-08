package org.codehaus.xfire.transport;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple Session implementation backed by an unsyncrhonized map.
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class MapSession implements Session
{
    private Map values = new HashMap();

    public Object get( Object key ) {
        return values.get( key );
    }

    public void put( Object key, Object value ) {
        values.put( key, value );
    }
}
