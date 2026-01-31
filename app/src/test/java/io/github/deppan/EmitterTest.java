package io.github.deppan;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class EmitterTest {

    @Test
    public void testEmit() throws Exception {
        String uri = System.getenv("REDIS_URI");

        try (RedisClient redisClient = RedisClient.create(uri)) {
            StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(ByteArrayCodec.INSTANCE);
            RedisAsyncCommands<byte[], byte[]> commands = connect.async();
            PublishListener listener = new PublishListener() {

                @Override
                public void publish(String channel, byte[] msg) {
                    commands.publish(channel.getBytes(StandardCharsets.UTF_8), msg);
                }
            };
            HashMap<String, Object> map = new HashMap<>();
            map.put("content", "This message from java emitter.");
            Emitter emitter = new Emitter(listener, new EmitterOptions(), "/");
            emitter.emit("java-emitter", map);
        }
    }

    @Test
    public void testJoin() throws Exception {
        String uri = System.getenv("REDIS_URI");

        try (RedisClient redisClient = RedisClient.create(uri)) {
            StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(ByteArrayCodec.INSTANCE);
            RedisAsyncCommands<byte[], byte[]> commands = connect.async();
            PublishListener listener = new PublishListener() {

                @Override
                public void publish(String channel, byte[] msg) {
                    commands.publish(channel.getBytes(StandardCharsets.UTF_8), msg);
                }
            };

            Emitter emitter = new Emitter(listener, new EmitterOptions(), "/");
            emitter.socketsJoin("test");
        }
    }

    @Test
    public void testLeave() throws Exception {
        String uri = System.getenv("REDIS_URI");

        try (RedisClient redisClient = RedisClient.create(uri)) {
            StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(ByteArrayCodec.INSTANCE);
            RedisAsyncCommands<byte[], byte[]> commands = connect.async();
            PublishListener listener = new PublishListener() {

                @Override
                public void publish(String channel, byte[] msg) {
                    commands.publish(channel.getBytes(StandardCharsets.UTF_8), msg);
                }
            };

            Emitter emitter = new Emitter(listener, new EmitterOptions(), "/");
            emitter.socketsLeave("test");
        }
    }

    @Test
    public void testDisconnect() throws Exception {
        String uri = System.getenv("REDIS_URI");

        try (RedisClient redisClient = RedisClient.create(uri)) {
            StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(ByteArrayCodec.INSTANCE);
            RedisAsyncCommands<byte[], byte[]> commands = connect.async();
            PublishListener listener = new PublishListener() {

                @Override
                public void publish(String channel, byte[] msg) {
                    commands.publish(channel.getBytes(StandardCharsets.UTF_8), msg);
                }
            };

            Emitter emitter = new Emitter(listener, new EmitterOptions(), "/");
            emitter.disconnectSockets(true);
        }
    }
}