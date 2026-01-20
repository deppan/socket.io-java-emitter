package io.github.deppan;

public interface PublishListener {

    /**
     *
     * @param channel will be sent to
     * @param msg     either a String or a byte[]
     */
    void publish(String channel, Object msg);
}
