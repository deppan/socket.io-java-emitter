package io.github.deppan;

import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;

public class RedisClient {
    private JedisPool jedisPool = null;
    private RedisTemplate<String, byte[]> redisTemplate = null;

    public RedisClient(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public RedisClient(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publish(String channel, Object msg) {
        if (redisTemplate != null) {
            redisTemplate.convertAndSend(channel, msg);
        } else if (jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                if (msg instanceof byte[]) {
                    jedis.publish(channel.getBytes(StandardCharsets.UTF_8), (byte[]) msg);
                } else if (msg instanceof String) {
                    jedis.publish(channel.getBytes(StandardCharsets.UTF_8), ((String) msg).getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }
}
