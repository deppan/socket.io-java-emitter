package io.github.deppan.emitter;

import io.github.deppan.Emitter;
import io.github.deppan.EmitterOptions;
import io.github.deppan.RedisClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class IndexController {

    private final RedisTemplate<String, byte[]> redisTemplate;

    public IndexController(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/")
    public ResponseEntity<?> index() {
        RedisClient redisClient = new RedisClient(this.redisTemplate);
        Emitter io = new Emitter(redisClient, new EmitterOptions(), "/");
        io.in("a").emit("event", Map.of("hello", "world"));
        io.in("a").socketsLeave("a");
        io.in("a").emit("event", Map.of("hello", "hi"));
        io.in("b").emit("event", Map.of("hello", "world"));

        return ResponseEntity.ok("OK");
    }
}
