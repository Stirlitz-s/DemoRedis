package com.stsoft.demoredis;

import org.junit.Test;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.codec.RedisCodec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;


public class RedisMapTestStrStrKeySetRemove {
    @Test
    public void mapTestKeySetRemove() {
        RedisClient redisClient = new RedisClient(RedisURI.create("redis://127.0.0.1:6379"));
        RedisCodec<String, Object> redisCodec = new CodecStrStr(); 
        RedisConnection<String, Object> connection = redisClient.connect(redisCodec);

        RedisMap<String> actual = new RedisMap<>(connection);
        actual.clear();
        actual.put("zero", "0");
        actual.put("one", "1");
        actual.put("two", "2");
        actual.put("three", "3");
        actual.put("four", "4");
        actual.put("five", "5");
        actual.put("six", "0");
        
        HashMap<String, String> expected = new HashMap<>();
        expected.put("zero", "0");
        expected.put("one", "1");
        expected.put("two", "2");
        expected.put("three", "3");
        expected.put("four", "4");
        expected.put("five", "5");
        expected.put("six", "0");
       
        Set<String> actualKeySet = actual.keySet();
        Set<String> expectedKeySet = expected.keySet();
        
        System.out.println(actualKeySet.remove("zero"));
        System.out.println(actualKeySet.remove("one"));
        System.out.println(actualKeySet.remove("six"));
        expectedKeySet.remove("zero");
        expectedKeySet.remove("one");
        expectedKeySet.remove("six");
        
        assertEquals(actual.size(), expected.size());    
        for(Entry<String, String> value:expected.entrySet()) {
            String expectedValue = expected.get(value.getKey());
            String actualValue = actual.get(value.getKey());
            assertNotNull(actualValue);
            assertEquals(value.getValue(), actualValue, expectedValue);
        }
        connection.close();
    }
}
