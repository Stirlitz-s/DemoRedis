package com.stsoft.demoredis;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.codec.RedisCodec;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ConnectToRedis {

    public static void main(String[] args) {
        RedisClient redisClient = new RedisClient(RedisURI.create("redis://127.0.0.1:6379"));
        RedisCodec<String, Object> redisCodec = new CodecStrInt(); 
        RedisConnection<String, Object> connection = redisClient.connect(redisCodec);

        RedisMap<Integer> testMap = new RedisMap<>(connection);
        testMap.clear();
        testMap.put("zero", 0);
        testMap.put("one", 1);
        testMap.put("two", 2);
        testMap.put("three", 3);
        testMap.put("four", 4);
        testMap.put("five", 5);
        testMap.put("six", 0);
        printMap(testMap, "RedisMap");
        Collection<?> testSet = testMap.values();
        printSet(testSet);
        System.out.println("retainAll " + testSet.retainAll(Arrays.asList(0, 4)));
        printSet(testSet);
        printMap(testMap, "RedisMap");
        connection.close();
    }
    static void printSet(Collection<?> inpSet) {
        System.out.println("**********Set*************");
        inpSet.forEach(System.out::println);
        System.out.println("**********/Set*************");
    }

    static void printMap(Map<String, ?> inpMap, String name) {
        System.out.println("**********Map "+name + " *************");
        inpMap.entrySet().stream().forEach(System.out::println);
        System.out.println("**********/Map " + name + "*************");
    }
}
