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
        RedisCodec<String, Integer> redisCodec = new CodecStrInt(); 
        RedisConnection<String, Integer> connection = redisClient.connect(redisCodec);

        RedisMap<String, Integer> testMap = new RedisMap<>(connection);
        testMap.clear();
     //   HashMap<String, Integer> testMap = new HashMap<>();
        System.out.println(testMap.put("zero", 0));
        System.out.println(testMap.put("one", 1));
        System.out.println(testMap.put("two", 2));
        System.out.println(testMap.put("three", 3));
        System.out.println(testMap.put("four", 4));
        System.out.println(testMap.put("five", 5));
        System.out.println(testMap.put("six", 0));
        printMap(testMap);
        Collection<Integer> testSet = testMap.values();
        printSet(testSet);
        System.out.println("retainAll " + testSet.retainAll(Arrays.asList(0, 4)));
        printSet(testSet);
        printMap(testMap);
        connection.close();
    }
    static void printSet(Collection inpSet) {
        System.out.println("**********Set*************");
        inpSet.stream().forEach(System.out::println);
        System.out.println("**********/Set*************");
    }

    static void printMap(Map inpMap) {
        System.out.println("**********Map*************");
        inpMap.values().stream().forEach(System.out::println);
        System.out.println("**********/Map*************");
    }
}
