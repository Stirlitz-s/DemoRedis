package com.stsoft.demoredis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Test;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.codec.RedisCodec;

public class RedisMapTestValuesSetRemove {
    @Test
    public void mapTestKeySetAddRemove() {
        RedisClient redisClient = new RedisClient(
        RedisURI.create("redis://127.0.0.1:6379"));
        RedisCodec<String, Integer> redisCodec = new CodecStrInt(); 
        RedisConnection<String, Integer> connection = redisClient.connect(redisCodec);

        RedisMap<String, Integer> actual = new RedisMap<>(connection);
        actual.clear();
        System.out.println(actual.put("zero", 0));
        System.out.println(actual.put("one", 1));
        System.out.println(actual.put("two", 2));
        System.out.println(actual.put("three", 3));
        System.out.println(actual.put("four", 4));
        System.out.println(actual.put("five", 5));
        System.out.println(actual.put("six", 0));
        
        HashMap<String, Integer> expected = new HashMap<>();
        System.out.println(expected.put("zero", 0));
        System.out.println(expected.put("one", 1));
        System.out.println(expected.put("two", 2));
        System.out.println(expected.put("three", 3));
        System.out.println(expected.put("four", 4));
        System.out.println(expected.put("five", 5));
        System.out.println(expected.put("six", 0));
        
        Collection<Integer> actualKeySet = (Collection<Integer>) actual.values();
        Collection<Integer> expectedKeySet = (Collection<Integer>) expected.values();
        
        actualKeySet.remove(0);
        actualKeySet.remove(1);
        actualKeySet.remove(0);
        expectedKeySet.remove(0);
        expectedKeySet.remove(1);
        expectedKeySet.remove(0);
        
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
