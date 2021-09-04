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

import com.lambdaworks.redis.KeyScanCursor;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.ScanArgs;

public class RedisIterator<E> implements Iterator<Object> {
    
    private RedisConnection<String, Object> connection;
    private IteratorType type;
    private KeyScanCursor<String> cursor = null;
    private String prevKey = null;
    private ScanArgs scanArgs = null;
    private int currentIndexOfList = 0;
    
    
    public RedisIterator (RedisConnection<String, Object> connection, IteratorType type) {
        this.connection = connection;
        this.type = type;
        this.scanArgs = ScanArgs.Builder.limit(10);
        this.cursor = connection.scan(this.cursor, scanArgs);
    }

    @Override
    public boolean hasNext() {
        if (cursor == null)
            return false;
        return !(cursor.isFinished() && (currentIndexOfList >= cursor.getKeys().size()));
    }

    @Override
    public Object next() {
        if (cursor.isFinished() && (currentIndexOfList >= cursor.getKeys().size())) {
            throw new NoSuchElementException();
        }
        
        String key = cursor.getKeys().get(currentIndexOfList);
        currentIndexOfList++;
        if (currentIndexOfList >= cursor.getKeys().size() && !cursor.isFinished()) {
            cursor = connection.scan(cursor, scanArgs);
            currentIndexOfList = 0;
        } 
        prevKey = new String(key);
        cursor = connection.scan(cursor, scanArgs);
        switch (type) {
            case KEYSET:
                return key;
            case ENTRYSET:;
                Map.Entry<String, Object> entry = new AbstractMap.SimpleEntry<>(key, connection.get(key));
                return entry;
            case VALUES:;
                return connection.get(key);
            default:
                throw new NoSuchElementException();
        }
    }
    
    public String getCurrentKey() {
        return prevKey;
    }

    public int getListLength() {
        return connection.dbsize().intValue();
    }    
}
