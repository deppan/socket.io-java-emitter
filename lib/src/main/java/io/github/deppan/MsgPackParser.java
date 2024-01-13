package io.github.deppan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class MsgPackParser implements Parser {

    private final ObjectMapper objectMapper;

    public MsgPackParser() {
        objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public byte[] encode(Object msg) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(msg);
    }
}
