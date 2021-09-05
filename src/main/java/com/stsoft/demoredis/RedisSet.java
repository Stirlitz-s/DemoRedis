/*
 * The class that provides to use Redis database as a java.util.Set
 * Default constructor creates connection to a local base and ENTRYSET type.
 * 
 */
package com.stsoft.demoredis;
import com.lambdaworks.redis.RedisConnection;

import java.util.*;

public class RedisSet<E> implements Set<E> {
    private RedisConnection<String, Object> connection;
    private final IteratorType type;
    

    public RedisSet(RedisConnection<String, Object> connection, IteratorType type) {
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

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        switch (type) {
            case KEYSET:
                return connection.exists((String) o);
            case ENTRYSET:
                return connection.exists(((Map.Entry<String, Object>) o).getKey());
            case VALUES:
                RedisIterator<?> iterator = (RedisIterator<?>) iterator();
                while (iterator.hasNext()) {
                    if ((iterator.next()).equals(o)) {
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
    public Iterator<E> iterator() {
        return new RedisIterator<>(connection, type);
    }

    @Override
    public Object[] toArray() {
        switch (type) {
            case KEYSET:
                return connection.keys("*").toArray();
            case ENTRYSET, VALUES:
                RedisIterator<?> iterator = (RedisIterator<?>) iterator();
                int len = iterator.getListLength();
                Object[] res = new Object[len];
                int n = 0;
                while (iterator.hasNext()) {
                    res[n] = iterator.next();
                    n++;
                }
                return res;
            default:
                return new Object[0];
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean add(Object e) {
        switch (type) {
            case KEYSET:
                return connection.set((String) e, 0) != null;
            case ENTRYSET:
                 return connection.set(((Map.Entry<String, Object>) e).getKey(),
                         ((Map.Entry<String, Object>) e).getValue()) != null;
            case VALUES:
                String key = UUID.randomUUID().toString();
                while (!connection.exists(key)) {
                    key = UUID.randomUUID().toString();
                }
                return connection.set(key, e) != null;
            default:
                return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        switch (type) {
        case KEYSET:
            return connection.del((String) o) != null;
        case ENTRYSET:
             return connection.del(((Map.Entry<String, Object>) o).getKey()) != null;
        case VALUES:
            RedisIterator<?> iterator = (RedisIterator<?>) iterator();
            while (iterator.hasNext()) {
                if ((iterator.next()).equals(o)) {
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
    public boolean addAll(Collection<? extends E> c) {
        try {
            c.forEach(this::add);
        } catch (Exception ex) {
            return false;    
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean result = false;
        RedisIterator<?> iterator = (RedisIterator<?>) iterator();
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        switch (type) {
            case KEYSET:
                return (T[]) connection.keys("*").toArray((String[]) a);
            case ENTRYSET, VALUES:
                RedisIterator<?> iterator = (RedisIterator<?>) iterator();
                int len = iterator.getListLength();
                Object[] res = len <= a.length ? a : new Object[len];
                int n = Math.max(len, a.length);
                for (int i = 0; i < n; i++) {
                    if (iterator.hasNext()) {
                        res[i] = iterator.next();
                    } else {
                        res[i] = null;
                    }
                }
                return (T[]) res;
            default:
                return (T[]) new Object[0];
        }
    }
    
    public RedisConnection<String, Object> getConnection() {
        return connection;
    }

    public void setConnection(RedisConnection<String, Object> connection) {
        this.connection = connection;
    }

}
