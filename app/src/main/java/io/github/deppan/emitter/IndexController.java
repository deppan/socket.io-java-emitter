package io.github.deppan.emitter;

import io.github.deppan.Emitter;
import io.github.deppan.EmitterOptions;
import io.github.deppan.PublishListener;
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
        PublishListener publishListener = new PublishListener() {
            @Override
            public void publish(String channel, Object msg) {
                redisTemplate.convertAndSend(channel, msg);
            }
        };
        Emitter io = new Emitter(publishListener, new EmitterOptions(), "/");
        io.in("a").emit("event", Map.of("a", "b"));

        io.serverSideEmit("forward", Map.of("hello", "world"));
        io.in("a").disconnectSockets(false);

        return ResponseEntity.ok("OK");
    }
}
