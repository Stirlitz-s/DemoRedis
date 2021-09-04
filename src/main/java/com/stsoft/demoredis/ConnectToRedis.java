package com.stsoft.demoredis;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.codec.RedisCodec;

public class ConnectToRedis {

    public static void main(String[] args) {
        RedisClient redisClient = new RedisClient(
        RedisURI.create("redis://127.0.0.1:6379"));
        RedisCodec<String, Object> redisCodec = new CodecStrInt(); 
        RedisConnection<String, Object> connection = redisClient.connect(redisCodec);

        RedisMap<String, Integer> testMap = new RedisMap<>(connection);
        testMap.clear();
        testMap.put("zero", 0);
        testMap.put("one", 1);
        testMap.put("two", 2);
        testMap.put("three", 3);
        testMap.put("four", 4);
        testMap.put("five", 5);
        testMap.put("six", 0);
        printMap(testMap, "RedisMap");
        Collection<Integer> testSet = testMap.values();
        printSet(testSet);
        System.out.println("retainAll " + testSet.retainAll(Arrays.asList(0, 4)));
        printSet(testSet);
        printMap(testMap, "RedisMap");
        connection.close();
    }
    static void printSet(Collection inpSet) {
        System.out.println("**********Set*************");
        inpSet.stream().forEach(System.out::println);
        System.out.println("**********/Set*************");
    }

    static void printMap(Map inpMap, String name) {
        System.out.println("**********Map "+name + " *************");
        inpMap.entrySet().stream().forEach(System.out::println);
        System.out.println("**********/Map " + name + "*************");
    }
}
