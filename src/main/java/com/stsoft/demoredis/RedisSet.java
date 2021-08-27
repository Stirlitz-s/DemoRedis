/*
 * The class that provides to use Redis database as a java.util.Set
 * Default constructor creates connection to a local base and ENTRYSET type.
 * 
 */
package com.stsoft.demoredis;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.codec.RedisCodec;

public class RedisSet<T> implements Set<Object> {
    private RedisConnection<String, Integer> connection;
    private IteratorType type;
    
    /*
     * Default constructor creates connection to a local base
     * redis://127.0.0.1:6379
     * 
     */    
    public RedisSet() {
        RedisClient redisClient = new RedisClient(
        RedisURI.create("redis://127.0.0.1:6379"));
        RedisCodec<String, Integer> redisCodec = new CodecStrInt(); 
        RedisConnection<String, Integer> connection = redisClient.connect(redisCodec);
        this.connection = connection;
        this.type = type;
    }

    public RedisSet(RedisConnection<String, Integer> connection, IteratorType type) {
        this.connection = connection;
        this.type = type;
    }

    @Override
    public int size() {
        return connection.dbsize().intValue();
    }

    @Override
    public boolean isEmpty() {
        return connection.dbsize() == 0L;
    }

    @Override
    public boolean contains(Object o) {
        switch (type) {
            case KEYSET:;
                return connection.exists((String) o);
            case ENTRYSET:;
                return connection.exists(((Map.Entry<String, Integer>) o).getKey());
            case VALUES:;
                RedisIterator iterator = (RedisIterator) iterator();
                while (iterator.hasNext()) {
                    if (((Integer)iterator.next()).equals((Integer)o)) {
                        return true;
                    }
                }
            default:
                return false;
        }
    }


    @Override
    public void clear() {
        connection.flushall();
    }

    @Override
    public Iterator<Object> iterator() {
        return new RedisIterator(connection, type);
    }

    @Override
    public Object[] toArray() {
        switch (type) {
            case KEYSET:
                return connection.keys("*").toArray();
            case ENTRYSET, VALUES:
                RedisIterator iterator = (RedisIterator) iterator();
                int len = iterator.getListLength();
                Object[] res = new Object[len];
                int n = 0;
                while (iterator.hasNext()) {
                    res[n] = iterator.next();
                    n++;
                }
                return res;
            default:
                return null;
        }
    }

    @Override
    public boolean add(Object e) {
        switch (type) {
            case KEYSET:;
                return connection.set((String) e, 0) != null;
            case ENTRYSET:;
                 return connection.set(((Map.Entry<String, Integer>) e).getKey(),
                         ((Map.Entry<String, Integer>) e).getValue()) != null;
            case VALUES:;
                String key = UUID.randomUUID().toString();
                while (!connection.exists(key)) {
                    key = UUID.randomUUID().toString();
                }
                return connection.set(key, (Integer) e) != null;
            default:
                return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        switch (type) {
        case KEYSET:
            return connection.del((String) o) != null;
        case ENTRYSET:
             return connection.del(((Map.Entry<String, Integer>) o).getKey()) != null;
        case VALUES:
            RedisIterator iterator = (RedisIterator) iterator();
            while (iterator.hasNext()) {
                if (((Integer)iterator.next()).equals((Integer)o)) {
                    return connection.del(iterator.getCurrentKey()) != null;
                }
            }
            return false;
        default:
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object buf : c) {
            if (!contains(buf))  
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        try {
            c.stream().forEach(buf -> add(buf));
        } catch (Exception ex) {
            return false;    
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean result = false;
        Object[] res = c.toArray();
        boolean[] isHere = new boolean[c.size()];
        RedisIterator iterator = (RedisIterator) iterator();
        while (iterator.hasNext()) {
            Object buf = iterator.next();
            if (!c.contains(buf)) {
                remove(buf);
                result = true;
            } 
        }
        return result;
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
        return connection.flushall() != null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        switch (type) {
            case KEYSET:
                return connection.keys("*").toArray(a);
            case ENTRYSET, VALUES:
                RedisIterator iterator = (RedisIterator) iterator();
                int len = iterator.getListLength();
                Object[] res = len <= a.length ? a : new Object[len];
                int n = len <= a.length ? a.length : len;
                for (int i = 0; i < n; i++) {
                    if (iterator.hasNext()) {
                        res[i] = iterator.next();
                    } else {
                        res[i] = null;
                    }
                }
                return (T[]) res;
            default:
                return null;
        }
    }
    
    public RedisConnection<String, Integer> getConnection() {
        return connection;
    }

    public void setConnection(RedisConnection<String, Integer> connection) {
        this.connection = connection;
    }

}
