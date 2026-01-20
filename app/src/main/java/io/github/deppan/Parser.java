package io.github.deppan;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Parser {

    byte[] encode(Object msg) throws JsonProcessingException;

    String stringify(Object msg) throws JsonProcessingException;
}
