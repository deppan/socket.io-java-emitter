package io.github.deppan;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MsgPackParserTests {

    @Test
    public void testParser() throws JsonProcessingException {
        ArrayList<String> rooms = new ArrayList<>();
        ArrayList<String> exceptRooms = new ArrayList<>();
        boolean close = false;

        Map<String, Object> map = new HashMap<String, Object>() {{
            put("type", RequestType.REMOTE_DISCONNECT.value);
            put("opts", new HashMap<String, Object>() {{
                put("rooms", new ArrayList<>(rooms));
                put("except", new ArrayList<>(exceptRooms));
            }});
            put("close", close);
        }};

        MsgPackParser parser = new MsgPackParser();
        String json = parser.stringify(map);
        System.out.println(json);

        System.out.println(new String(parser.encode(map)));
    }
}
