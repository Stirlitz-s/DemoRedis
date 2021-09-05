package com.stsoft.demoredis;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.lambdaworks.redis.codec.RedisCodec;

public class CodecStrStr extends RedisCodec<String, Object> {
    
    @Override
    public String decodeKey(ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
    }

    @Override
    public Object decodeValue(ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
    }

    @Override
    public byte[] encodeKey(final String key) {
        return StandardCharsets.UTF_8.encode(key).array();
    }

    @Override
    public byte[] encodeValue(final Object value) {
        return StandardCharsets.UTF_8.encode((String)value).array();
    }
}
