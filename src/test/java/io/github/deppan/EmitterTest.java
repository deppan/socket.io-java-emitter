package io.github.deppan;

import io.github.cdimascio.dotenv.Dotenv;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class EmitterTest {
    private final String uri;
    private final String key;

    public EmitterTest() {
        Dotenv dotenv = Dotenv.load();
        uri = dotenv.get("REDIS_URI");
        key = dotenv.get("KEY");
    }

    @Test
    public void testEmit() throws Exception {
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
            Emitter emitter = new Emitter(listener, new EmitterOptions(key), "/");
            emitter.emit("java-emitter", map);
        }

        try (redis.clients.jedis.RedisClient redisClient = redis.clients.jedis.RedisClient.create(uri)) {
            PublishListener listener = new PublishListener() {

                @Override
                public void publish(String channel, byte[] msg) {
                    redisClient.publish(channel.getBytes(StandardCharsets.UTF_8), msg);
                }
            };
            HashMap<String, Object> map = new HashMap<>();
            map.put("content", "This message from java emitter.");
            Emitter emitter = new Emitter(listener, new EmitterOptions(key), "/");
            emitter.emit("java-emitter", map);
        }
    }

    @Test
    public void testJoin() throws Exception {
        try (RedisClient redisClient = RedisClient.create(uri)) {
            StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(ByteArrayCodec.INSTANCE);
            RedisAsyncCommands<byte[], byte[]> commands = connect.async();
            PublishListener listener = new PublishListener() {

                @Override
                public void publish(String channel, byte[] msg) {
                    commands.publish(channel.getBytes(StandardCharsets.UTF_8), msg);
                }
            };

            Emitter emitter = new Emitter(listener, new EmitterOptions(key), "/");
            emitter.socketsJoin("test");

            HashMap<String, Object> map = new HashMap<>();
            map.put("content", "This message from test of java emitter.");
            emitter.in("test").emit("hi", map);
        }
    }

    @Test
    public void testLeave() throws Exception {
        try (RedisClient redisClient = RedisClient.create(uri)) {
            StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(ByteArrayCodec.INSTANCE);
            RedisAsyncCommands<byte[], byte[]> commands = connect.async();
            PublishListener listener = new PublishListener() {

                @Override
                public void publish(String channel, byte[] msg) {
                    commands.publish(channel.getBytes(StandardCharsets.UTF_8), msg);
                }
            };

            Emitter emitter = new Emitter(listener, new EmitterOptions(key), "/");
            emitter.socketsLeave("test");
        }
    }

    @Test
    public void testDisconnect() throws Exception {
        try (RedisClient redisClient = RedisClient.create(uri)) {
            StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(ByteArrayCodec.INSTANCE);
            RedisAsyncCommands<byte[], byte[]> commands = connect.async();
            PublishListener listener = new PublishListener() {

                @Override
                public void publish(String channel, byte[] msg) {
                    commands.publish(channel.getBytes(StandardCharsets.UTF_8), msg);
                }
            };

            Emitter emitter = new Emitter(listener, new EmitterOptions(key), "/");
            emitter.disconnectSockets(true);
        }
    }

    @Test
    public void testServerSideEmit() throws Exception {
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
            map.put("message", "This message from java emitter.");
            Emitter emitter = new Emitter(listener, new EmitterOptions(key), "/");
            emitter.serverSideEmit("hello", map);
        }
    }
}