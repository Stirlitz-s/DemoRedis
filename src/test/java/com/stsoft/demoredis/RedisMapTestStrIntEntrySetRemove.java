package com.stsoft.demoredis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.codec.RedisCodec;

public class RedisMapTestStrIntEntrySetRemove {
    @Test
    public void mapTestEntrySetRemove() {
        RedisClient redisClient = new RedisClient(
        RedisURI.create("redis://127.0.0.1:6379"));
        RedisCodec<String, Object> redisCodec = new CodecStrInt(); 
        RedisConnection<String, Object> connection = redisClient.connect(redisCodec);

        RedisMap<Integer> actual = new RedisMap<>(connection);
        actual.clear();
        actual.put("zero", 0);
        actual.put("one", 1);
        actual.put("two", 2);
        actual.put("three", 3);
        actual.put("four", 4);
        actual.put("five", 5);
        actual.put("six", 0);
        
        HashMap<String, Integer> expected = new HashMap<>();
        expected.put("zero", 0);
        expected.put("one", 1);
        expected.put("two", 2);
        expected.put("three", 3);
        expected.put("four", 4);
        expected.put("five", 5);
        expected.put("six", 0);
        
        Set<Entry<String, Integer>> actualEntrySet = actual.entrySet();
        Set<Entry<String, Integer>> expectedEntrySet = expected.entrySet();
        
        actualEntrySet.remove(new AbstractMap.SimpleEntry<>("zero", 0));
        actualEntrySet.remove(new AbstractMap.SimpleEntry<>("one", 1));
        actualEntrySet.remove(new AbstractMap.SimpleEntry<>("six", 0));
        expectedEntrySet.remove(new AbstractMap.SimpleEntry<>("zero", 0));
        expectedEntrySet.remove(new AbstractMap.SimpleEntry<>("one", 1));
        expectedEntrySet.remove(new AbstractMap.SimpleEntry<>("six", 0));
                
        assertEquals(actual.size(), expected.size());    
        for(Entry<String, Integer> value:expected.entrySet()) {
            Integer expectedValue= expected.get(value.getKey());
            Integer actualValue= actual.get(value.getKey());
            assertNotNull(actualValue);
            assertEquals(value.getValue(), actualValue, expectedValue);
        }
        connection.close();
    }
}
