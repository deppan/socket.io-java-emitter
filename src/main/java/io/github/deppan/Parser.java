package io.github.deppan;

public interface Parser {

    byte[] encode(Object msg) throws Exception;

    String stringify(Object msg) throws Exception;
}
