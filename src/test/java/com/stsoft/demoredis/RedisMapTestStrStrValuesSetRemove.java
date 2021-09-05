package com.stsoft.demoredis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Test;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.codec.RedisCodec;

public class RedisMapTestStrStrValuesSetRemove {
    @Test
    public void mapTestValuesSetRemove() {
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
       
        Collection<String> actualKeySet = actual.values();
        Collection<String> expectedKeySet = expected.values();
        
        actualKeySet.remove("0");
        actualKeySet.remove("1");
        actualKeySet.remove("0");
        expectedKeySet.remove("0");
        expectedKeySet.remove("1");
        expectedKeySet.remove("0");
        
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
