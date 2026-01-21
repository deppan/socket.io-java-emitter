package io.github.deppan;

public class EmitterOptions {
    /**
     * default "socket.io"
     */
    private String key;
    /**
     * default MsgPackParser
     */
    private Parser parser;

    public EmitterOptions() {
    }

    public EmitterOptions(String key) {
        this.key = key;
    }

    public EmitterOptions(String key, Parser parser) {
        this.key = key;
        this.parser = parser;
    }

    public String key() {
        return key;
    }

    public void key(String key) {
        this.key = key;
    }

    public Parser parser() {
        return parser;
    }

    public void parser(Parser parser) {
        this.parser = parser;
    }
}
