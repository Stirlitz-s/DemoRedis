package com.stsoft.demoredis;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import com.lambdaworks.redis.codec.RedisCodec;

public class CodecStrInt extends RedisCodec<String, Object> {
    
    @Override
    public String decodeKey(ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
    }

    @Override
    public Object decodeValue(ByteBuffer bytes) {
        final CharBuffer charSequence = StandardCharsets.UTF_8.decode(bytes);
        return Integer.parseInt(charSequence, 0, charSequence.length(), 10);
    }

    @Override
    public byte[] encodeKey(final String key) {
        return StandardCharsets.UTF_8.encode(key).array();
    }

    @Override
    public byte[] encodeValue(final Object value) {
        return ByteBuffer.wrap(Integer.toString((Integer)value).getBytes()).array();
    }
}
