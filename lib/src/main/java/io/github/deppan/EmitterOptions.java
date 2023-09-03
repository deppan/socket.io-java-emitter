package io.github.deppan;

public class EmitterOptions {
    /**
     * default "socket.io"
     */
    String key = null;
    Parser parser = null;

    public EmitterOptions() {
    }

    public EmitterOptions(String key, Parser parser) {
        this.key = key;
        this.parser = parser;
    }
}
