package com.stsoft.demoredis;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import com.lambdaworks.redis.codec.RedisCodec;

public class CodecStrInt extends RedisCodec<String, Integer> {
    
    @Override
    public String decodeKey(ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
    }

    @Override
    public Integer decodeValue(ByteBuffer bytes) {
        final CharBuffer charSequence = StandardCharsets.UTF_8.decode(bytes);
        return Integer.parseInt(charSequence, 0, charSequence.length(), 10);
    }

    @Override
    public byte[] encodeKey(final String key) {
        return StandardCharsets.UTF_8.encode(key).array();
    }

    @Override
    public byte[] encodeValue(final Integer value) {
        return ByteBuffer.wrap(Integer.toString(value).getBytes()).array();
    }

    /*@Override
    public String decodeKey(ByteBuffer bytes) {
        System.out.println("********decode Key********");
        return new String(bytes.array());
    }

    @Override
    public Integer decodeValue(ByteBuffer bytes) {
        System.out.println("********decode value********");
        System.out.println(bytes + " - value to decode");
        return bytes.getInt();
    }

    @Override
    public byte[] encodeKey(String key) {
        System.out.println("********encode Key********");
        return key.getBytes();
    }

    @Override
    public byte[] encodeValue(Integer value) {
        System.out.println("********encode Value********");
        System.out.println(value + " - value to encode");
        byte[] buf2 = ByteBuffer.allocate(4).putInt(value.intValue()).array();
        for (int i = 0; i < buf2.length; i++) {
            System.out.println(buf2[i] + " - buf2 element " + (i+1));
        }
        return buf2;
   //     return ByteBuffer.allocate(4).putInt(value.intValue()).array();
    }*/
    
}
