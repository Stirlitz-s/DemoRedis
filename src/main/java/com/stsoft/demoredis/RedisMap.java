/*
 * The class that provides to use Redis database as a java.util.Map
 * Default constructor creates connection to a local base.
 * 
 */

package com.stsoft.demoredis;
import com.lambdaworks.redis.RedisConnection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RedisMap<V> implements Map<String, V> {
    private RedisConnection<String, Object> connection;
    
    private final RedisSet<String> keySet;
    private final RedisSet<?> valuesSet;
    private final RedisSet<?> entrySet;
    
    @SuppressWarnings("hiding")
    public <K,V>RedisMap (RedisConnection<String, Object> connection) {
        this.connection = connection;
        keySet = new RedisSet<>(connection, IteratorType.KEYSET);
        entrySet = new RedisSet<>(connection, IteratorType.ENTRYSET);
        valuesSet = new RedisSet<>(connection, IteratorType.VALUES);
        
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

    @SuppressWarnings("unchecked")
    public V get(Object key) {
        return (V) connection.get(key.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object key) {
        Object res = connection.get((String) key);
        connection.del((String)key);     
        return (V) res;
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        m.forEach((key, value) -> connection.set(key, value));
    }

    public void clear() {
        connection.flushall();
    }

    public Set<String> keySet() {
        return keySet;
    }

    @SuppressWarnings("unchecked")
    public Collection<V> values() {
        return (Collection<V>) valuesSet;
    }

    @SuppressWarnings("unchecked")
    public Set<Entry<String, V>> entrySet() {
        return (Set<Entry<String, V>>) entrySet;
    }


    @SuppressWarnings("unchecked")
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
