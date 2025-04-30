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
        io.in("O3w3DKhD-2VJmuFgAAAB").emit("event", Map.of("hello", "world"));
        io.in("O3w3DKhD-2VJmuFgAAAB").socketsJoin("a");
        io.in("a").emit("event", Map.of("a", "b"));

        io.serverSideEmit("forward", Map.of("hello", "world"));
        io.in("a").disconnectSockets(false);

        return ResponseEntity.ok("OK");
    }
}
