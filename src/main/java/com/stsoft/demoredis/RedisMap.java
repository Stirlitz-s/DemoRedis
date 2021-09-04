/*
 * The class that provides to use Redis database as a java.util.Map
 * Default constructor creates connection to a local base.
 * 
 */

package com.stsoft.demoredis;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.lambdaworks.redis.*;

public class RedisMap<K,V> implements Map<String, V> {
    private RedisConnection<String, Object> connection;
    
    private RedisSet<String> keySet;
    private RedisSet<Object> valuesSet;
    private RedisSet<Entry<String, Object>> entrySet;   
    
    public <K,V>RedisMap (RedisConnection<String, Object> connection) {
        this.connection = connection;
        keySet = new RedisSet<String>(connection, IteratorType.KEYSET);
        entrySet = new RedisSet<Entry<String, Object>>(connection, IteratorType.ENTRYSET);
        valuesSet = new RedisSet<Object>(connection, IteratorType.VALUES);
        
    }

    public int size() {
        return connection.dbsize().intValue();
    }

    public boolean isEmpty() {
        return connection.dbsize() == 0L;
    }

    public boolean containsKey(Object key) {
        return connection.exists(key.toString());
    }

    public boolean containsValue(Object value) {
        return valuesSet.contains(value);
    }

    public V get(Object key) {
        return (V) connection.get(key.toString());
    }

    @Override
    public V remove(Object key) {
        Object res = connection.get((String) key);
        connection.del((String)key);     
        return (V) res;
    }

    public void putAll(Map m) {
        m.forEach((key, value) -> connection.set(key.toString(), value));
    }

    public void clear() {
        connection.flushall();
    }

    public Set keySet() {
        return keySet;
    }

    public Collection values() {
        return valuesSet;
    }

    public Set entrySet() {
        return entrySet;
    }


    @Override
    public V put(String key, Object value) {
        if (connection.set(key, value).equals("OK"))
            return (V) value;
        else
            return null;
    }

    public RedisConnection<String, Object> getConnection() {
        return connection;
    }

    /*
     * The connection sets to all used Redis-connection data types
     * 
     */
    public void setConnection(RedisConnection<String, Object> connection) {
        entrySet.setConnection(connection);
        keySet.setConnection(connection);
        valuesSet.setConnection(connection);
        this.connection = connection;
    }

}
