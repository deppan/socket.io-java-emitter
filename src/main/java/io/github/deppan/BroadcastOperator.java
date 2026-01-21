package io.github.deppan;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class BroadcastOperator {

    private final Set<String> reserved = new HashSet<String>() {{
        add("connect");
        add("connect_error");
        add("disconnect");
        add("disconnecting");
        add("newListener");
        add("removeListener");
    }};
    private final PublishListener publishListener;
    private final BroadcastOptions broadcastOptions;
    private Set<String> rooms = new HashSet<>();
    private Set<String> exceptRooms = new HashSet<>();
    private Map<String, Object> flags = new HashMap<>();

    public BroadcastOperator(PublishListener publishListener, BroadcastOptions broadcastOptions, Set<String> rooms, Set<String> exceptRooms, Map<String, Object> flags) {
        this.publishListener = publishListener;
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

    public BroadcastOperator(PublishListener publishListener, BroadcastOptions broadcastOptions) {
        this.publishListener = publishListener;
        this.broadcastOptions = broadcastOptions;
    }

    /**
     * Targets a room when emitting.
     *
     * @param room
     * @return a new BroadcastOperator instance
     */
    public BroadcastOperator to(String... room) {
        Set<String> rooms = new HashSet<>(this.rooms);
        Collections.addAll(rooms, room);
        return new BroadcastOperator(this.publishListener, this.broadcastOptions, rooms, this.exceptRooms, this.flags);
    }

    /**
     * Targets a room when emitting.
     *
     * @param room
     * @return a new BroadcastOperator instance
     */
    public BroadcastOperator in(String... room) {
        return this.to(room);
    }

    /**
     * Excludes a room when emitting.
     *
     * @param room
     * @return a new BroadcastOperator instance
     */
    public BroadcastOperator except(String... room) {
        Set<String> exceptRooms = new HashSet<>(this.exceptRooms);
        Collections.addAll(exceptRooms, room);
        return new BroadcastOperator(this.publishListener, this.broadcastOptions, this.rooms, exceptRooms, this.flags);
    }

    /**
     * Sets the compress flag.
     *
     * @param compress - if `true`, compresses the sending data
     * @return a new BroadcastOperator instance
     */
    public BroadcastOperator compress(boolean compress) {
        Map<String, Object> flags = new HashMap<>(this.flags);
        flags.put("compress", compress);
        return new BroadcastOperator(this.publishListener, this.broadcastOptions, this.rooms, this.exceptRooms, flags);
    }

    /**
     * Sets a modifier for a subsequent event emission that the event data may be lost if the client is not ready to
     * receive messages (because of network slowness or other issues, or because they’re connected through long polling
     * and is in the middle of a request-response cycle).
     *
     * @return a new BroadcastOperator instance
     */
    public BroadcastOperator volatile_() {
        Map<String, Object> flags = new HashMap<>(this.flags);
        flags.put("volatile", true);
        return new BroadcastOperator(this.publishListener, this.broadcastOptions, this.rooms, this.exceptRooms, flags);
    }

    /**
     * Emits to all clients.
     *
     * @return Always true
     */
    public boolean emit(String event, Object... args) {
        if (reserved.contains(event)) {
            throw new RuntimeException("\"" + event + "\" is a reserved event name");
        }
        List<Object> data = new ArrayList<>();
        data.add(event);
        Collections.addAll(data, args);
        Map<String, Object> packet = new HashMap<String, Object>() {{
            put("type", PacketType.EVENT);
            put("data", data);
            put("nsp", BroadcastOperator.this.broadcastOptions.nsp);
        }};
        Map<String, Object> ops = new HashMap<String, Object>() {{
            put("rooms", new ArrayList<>(BroadcastOperator.this.rooms));
            put("flags", BroadcastOperator.this.flags);
            put("except", new ArrayList<>(BroadcastOperator.this.exceptRooms));
        }};

        String channel = this.broadcastOptions.broadcastChannel;
        if (this.rooms.size() == 1) {
            channel += rooms.stream().collect(Collectors.joining("#", "", "#"));
        }
        try {
            byte[] msg = this.broadcastOptions.parser.encode(Arrays.asList(Emitter.UID, packet, ops));
            this.publishListener.publish(channel, msg);
            return true;
        } catch (Exception exception) {
            this.broadcastOptions.logger.error("emit", exception);
            return false;
        }
    }

    /**
     * Makes the matching socket instances join the specified rooms
     *
     * @param rooms
     */
    public void socketsJoin(String... rooms) {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("type", RequestType.REMOTE_JOIN);
            put("opts", new HashMap<String, Object>() {{
                put("rooms", new ArrayList<>(BroadcastOperator.this.rooms));
                put("except", new ArrayList<>(BroadcastOperator.this.exceptRooms));
            }});
            put("rooms", Arrays.asList(rooms));
        }};

        try {
            String request = this.broadcastOptions.parser.stringify(map);
            this.publishListener.publish(this.broadcastOptions.requestChannel, request.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            this.broadcastOptions.logger.error("socketsJoin", exception);
        }
    }

    /**
     * Makes the matching socket instances leave the specified rooms
     *
     * @param rooms
     */
    public void socketsLeave(String... rooms) {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("type", RequestType.REMOTE_LEAVE);
            put("opts", new HashMap<String, Object>() {{
                put("rooms", new ArrayList<>(BroadcastOperator.this.rooms));
                put("except", new ArrayList<>(BroadcastOperator.this.exceptRooms));
            }});
            put("rooms", Arrays.asList(rooms));
        }};

        try {
            String request = this.broadcastOptions.parser.stringify(map);
            this.publishListener.publish(this.broadcastOptions.requestChannel, request.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            this.broadcastOptions.logger.error("socketsLeave", exception);
        }
    }

    /**
     * Makes the matching socket instances disconnect
     *
     * @param close - whether to close the underlying connection
     */
    public void disconnectSockets(boolean close) {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("type", RequestType.REMOTE_DISCONNECT);
            put("opts", new HashMap<String, Object>() {{
                put("rooms", new ArrayList<>(BroadcastOperator.this.rooms));
                put("except", new ArrayList<>(BroadcastOperator.this.exceptRooms));
            }});
            put("close", close);
        }};

        try {
            String request = this.broadcastOptions.parser.stringify(map);
            this.publishListener.publish(this.broadcastOptions.requestChannel, request.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            this.broadcastOptions.logger.error("disconnectSockets", exception);
        }
    }
}
