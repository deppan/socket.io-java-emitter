package io.github.deppan;

import java.util.*;
import java.util.stream.Collectors;

public class BroadcastOperator {

    private final Set<String> reserved = Set.of(new String[]{
            "connect", "connect_error", "disconnect",
            "disconnecting", "newListener", "removeListener"});

    private final RedisClient redisClient;

    private final BroadcastOptions broadcastOptions;

    private Set<String> rooms = new HashSet<>();

    private Set<String> exceptRooms = new HashSet<>();

    private Map<String, Object> flags = new HashMap<>();

    public BroadcastOperator(RedisClient redisClient, BroadcastOptions broadcastOptions, Set<String> rooms, Set<String> exceptRooms, Map<String, Object> flags) {
        this.redisClient = redisClient;
        this.broadcastOptions = broadcastOptions;
        if (rooms != null) {
            this.rooms = rooms;
        }
        if (exceptRooms != null) {
            this.exceptRooms = exceptRooms;
        }
        if (flags != null) {
            this.flags = flags;
        }
    }

    public BroadcastOperator(RedisClient redisClient, BroadcastOptions broadcastOptions) {
        this.redisClient = redisClient;
        this.broadcastOptions = broadcastOptions;
    }

    public BroadcastOperator to(String... room) {
        Set<String> rooms = new HashSet<>(this.rooms);
        Collections.addAll(rooms, room);
        return new BroadcastOperator(this.redisClient, this.broadcastOptions, rooms, this.exceptRooms, this.flags);
    }

    public BroadcastOperator in(String... room) {
        return this.to(room);
    }

    public BroadcastOperator except(String... room) {
        Set<String> exceptRooms = new HashSet<>(this.exceptRooms);
        Collections.addAll(exceptRooms, room);
        return new BroadcastOperator(this.redisClient, this.broadcastOptions, this.rooms, exceptRooms, this.flags);
    }

    public BroadcastOperator compress(boolean compress) {
        Map<String, Object> flags = new HashMap<>(this.flags);
        flags.put("compress", compress);
        return new BroadcastOperator(this.redisClient, this.broadcastOptions, this.rooms, this.exceptRooms, flags);
    }

    public BroadcastOperator volatileFunc() {
        Map<String, Object> flags = new HashMap<>(this.flags);
        flags.put("volatile", "true");
        return new BroadcastOperator(this.redisClient, this.broadcastOptions, this.rooms, this.exceptRooms, flags);
    }

    public boolean emit(String event, Object... args) {
        if (reserved.contains(event)) {
            throw new RuntimeException("\"" + event + "\" is a reserved event name");
        }
        List<Object> data = new ArrayList<>();
        data.add(event);
        Collections.addAll(data, args);
        Map<String, Object> packet = Map.of("type", PacketType.EVENT.value, "data", data, "nsp", this.broadcastOptions.nsp);
        Map<String, Object> ops = Map.of("rooms", new ArrayList<>(this.rooms), "flags", this.flags, "except", new ArrayList<>(this.exceptRooms));

        String channel = this.broadcastOptions.broadcastChannel;
        if (this.rooms.size() == 1) {
            channel += rooms.stream().collect(Collectors.joining("#", "", "#"));
        }
        try {
            byte[] msg = this.broadcastOptions.parser.encode(Arrays.asList(Emitter.UID, packet, ops));
            this.redisClient.publish(channel, msg);
            return true;
        } catch (Exception exception) {
            this.broadcastOptions.logger.debug("emit: {}", exception.toString());
            return false;
        }
    }

    public void socketsJoin(String... room) {
        Map<String, Object> map = Map.of(
                "type", RequestType.REMOTE_JOIN.value,
                "opts", Map.of("rooms", new ArrayList<>(this.rooms), "except", new ArrayList<>(this.exceptRooms)),
                "rooms", room.length == 1 ? room[0] : Arrays.asList(room)
        );
        try {
            byte[] request = this.broadcastOptions.parser.encode(map);
            this.redisClient.publish(this.broadcastOptions.requestChannel, request);
        } catch (Exception exception) {
            this.broadcastOptions.logger.debug("socketsJoin: {}", exception.toString());
        }
    }

    public void socketsLeave(String... room) {
        Map<String, Object> map = Map.of(
                "type", RequestType.REMOTE_LEAVE.value,
                "opts", Map.of("rooms", new ArrayList<>(this.rooms), "except", new ArrayList<>(this.exceptRooms)),
                "rooms", Arrays.asList(room)
        );
        try {
            byte[] request = this.broadcastOptions.parser.encode(map);
            this.redisClient.publish(this.broadcastOptions.requestChannel, request);
        } catch (Exception exception) {
            this.broadcastOptions.logger.debug("socketsLeave: {}", exception.toString());
        }
    }

    public void disconnectSockets(boolean close) {
        Map<String, Object> map = Map.of(
                "type", RequestType.REMOTE_DISCONNECT.value,
                "opts", Map.of("rooms", new ArrayList<>(this.rooms), "except", new ArrayList<>(this.exceptRooms)),
                "close", close
        );
        try {
            byte[] request = this.broadcastOptions.parser.encode(map);
            this.redisClient.publish(this.broadcastOptions.requestChannel, request);
        } catch (Exception exception) {
            this.broadcastOptions.logger.debug("disconnectSockets: {}", exception.toString());
        }
    }
}
