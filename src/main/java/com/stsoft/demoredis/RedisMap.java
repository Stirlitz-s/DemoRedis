/*
 * The class that provides to use Redis database as a java.util.Map
 * Default constructor creates connection to a local base.
 * 
 */

package com.stsoft.demoredis;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//import java.util.*;
import com.lambdaworks.redis.*;
import com.lambdaworks.redis.codec.RedisCodec;

public class RedisMap<K,V> implements Map<String, Integer> {
    private RedisConnection<String, Integer> connection;
    
    private RedisSet<String> keySet;
    private RedisSet<Integer> valuesSet;
    private RedisSet<Map.Entry<String, Integer>> entrySet;

    
    
    public <K,V>RedisMap (RedisConnection<String, Integer> connection) {
        this.connection = connection;
        keySet = new RedisSet<String>(connection, IteratorType.KEYSET);
        entrySet = new RedisSet<Map.Entry<String, Integer>>(connection, IteratorType.ENTRYSET);
        valuesSet = new RedisSet<Integer>(connection, IteratorType.VALUES);
        
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

    public Integer get(Object key) {
        return connection.get(key.toString());
    }

    @Override
    public Integer remove(Object key) {
        Integer res = (Integer) connection.get((String) key);
        connection.del((String)key);     
        return res;
    }

    public void putAll(Map m) {
        m.forEach((key, value) -> connection.set(key.toString(), (Integer)value));
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
    public Integer put(String key, Integer value) {
        if (connection.set(key, value).equals("OK"))
            return value;
        else
            return null;
    }

    public RedisConnection<String, Integer> getConnection() {
        return connection;
    }

    /*
     * The connection sets to all used Redis-connection data types
     * 
     */
    public void setConnection(RedisConnection<String, Integer> connection) {
        entrySet.setConnection(connection);
        keySet.setConnection(connection);
        valuesSet.setConnection(connection);
        this.connection = connection;
    }

}
