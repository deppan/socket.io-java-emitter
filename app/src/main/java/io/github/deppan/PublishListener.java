package io.github.deppan;

public interface PublishListener {

    /**
     *
     * @param channel will be sent to
     * @param msg     msg
     */
    void publish(String channel, byte[] msg);
}
