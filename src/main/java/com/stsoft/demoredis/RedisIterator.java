/*
 * The class that provides to use iterator in Redis-connection data types
 * 
 */
package com.stsoft.demoredis;

import com.lambdaworks.redis.KeyScanCursor;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.ScanArgs;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RedisIterator<E> implements Iterator<E> {
    
    private final RedisConnection<String, Object> connection;
    private final IteratorType type;
    private KeyScanCursor<String> cursor = null;
    private String prevKey = null;
    private final ScanArgs scanArgs;
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

    @SuppressWarnings("unchecked")
    @Override
    public E next() {
        if (cursor.isFinished() && (currentIndexOfList >= cursor.getKeys().size())) {
            throw new NoSuchElementException();
        }
        
        String key = cursor.getKeys().get(currentIndexOfList);
        currentIndexOfList++;
        if (currentIndexOfList >= cursor.getKeys().size() && !cursor.isFinished()) {
            cursor = connection.scan(cursor, scanArgs);
            currentIndexOfList = 0;
        } 
        prevKey = key;
        cursor = connection.scan(cursor, scanArgs);
        return switch (type) {
            case KEYSET -> (E) key;
            case ENTRYSET -> (E) new AbstractMap.SimpleEntry<>(key, connection.get(key));
            case VALUES -> (E) connection.get(key);
        };
    }
    
    public String getCurrentKey() {
        return prevKey;
    }

    public int getListLength() {
        return connection.dbsize().intValue();
    }    
}
