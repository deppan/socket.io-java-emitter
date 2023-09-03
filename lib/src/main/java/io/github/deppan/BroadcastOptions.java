package io.github.deppan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadcastOptions {
    String nsp;
    String broadcastChannel;
    String requestChannel;
    Parser parser;
    Logger logger;

    public BroadcastOptions(String nsp, String broadcastChannel, String requestChannel, Parser parser) {
        this.nsp = nsp;
        this.broadcastChannel = broadcastChannel;
        this.requestChannel = requestChannel;
        this.parser = parser;
        this.logger = LoggerFactory.getLogger(Emitter.class);
    }
}
