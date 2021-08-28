/*
 * The class that provides to use iterator in Redis-connection data types
 * 
 */
package com.stsoft.demoredis;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.lambdaworks.redis.RedisConnection;

public class RedisIterator implements Iterator<Object> {
    
    private RedisConnection<String, Integer> connection;

    private int listLength;
    private int indexOfNext;
    private List<String> keys;
    private IteratorType type;
    
    
    public RedisIterator (RedisConnection<String, Integer> connection, IteratorType type) {
        this.connection = connection;
        this.type = type;
        this.keys = connection.keys("*");
        this.indexOfNext = 0;
        this.listLength = keys.size();
    }

    @Override
    public boolean hasNext() {
        return indexOfNext < listLength ? true : false;
    }

    @Override
    public Object next() {
        if (indexOfNext == listLength) {
            throw new NoSuchElementException();
        }
        String key = keys.get(indexOfNext);
        indexOfNext++;
        switch (type) {
            case KEYSET:
                return key;
            case ENTRYSET:;
                Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>(key, connection.get(key));
                return entry;
            case VALUES:;
                return connection.get(key);
            default:
                throw new NoSuchElementException();
        }
    }
    
    public String getCurrentKey() {
        return indexOfNext == 0 ? keys.get(indexOfNext) : keys.get(indexOfNext - 1);
    }

    public int getListLength() {
        return listLength;
    }

    
}
