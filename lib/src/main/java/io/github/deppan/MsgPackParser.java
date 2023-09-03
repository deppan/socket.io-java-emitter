package io.github.deppan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class MsgPackParser implements Parser {

    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    @Override
    public byte[] encode(Object msg) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(msg);
    }
}
