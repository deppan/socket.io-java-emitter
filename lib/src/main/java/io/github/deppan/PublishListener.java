package io.github.deppan;

public interface PublishListener {

    void publish(String channel, Object msg);
}
