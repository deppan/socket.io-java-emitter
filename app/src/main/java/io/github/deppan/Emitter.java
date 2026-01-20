package io.github.deppan;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Emitter {

    public final static String UID = "emitter";
    private final EmitterOptions opts;
    private final BroadcastOptions broadcastOptions;
    private final PublishListener publishListener;

    public Emitter(PublishListener publishListener) {
        this(publishListener, null, null);
    }

    public Emitter(PublishListener publishListener, EmitterOptions opts, String nsp) {
        if (nsp == null) {
            nsp = "/";
        }

        this.publishListener = publishListener;
        this.opts = new EmitterOptions("socket.io", new MsgPackParser());
        if (opts != null) {
            if (opts.key() != null) {
                this.opts.key(opts.key());
            }
            if (opts.parser() != null) {
                this.opts.parser(opts.parser());
            }
        }

        this.broadcastOptions = new BroadcastOptions(
                nsp,
                this.opts.key() + "#" + nsp + "#",
                this.opts.key() + "-request#" + nsp + "#",
                this.opts.parser()
        );
    }

    /**
     * Return a new emitter for the given namespace.
     *
     * @param nsp - namespace
     * @return - a emitter instance
     */
    public Emitter of(String nsp) {
        return new Emitter(this.publishListener, this.opts, (nsp.charAt(0) != '/' ? "/" : "") + nsp);
    }

    /**
     * Emits to all clients.
     *
     * @param event - identify
     * @param args  -
     * @return true
     */
    public boolean emit(String event, Object... args) {
        return new BroadcastOperator(this.publishListener, this.broadcastOptions).emit(event, args);
    }

    /**
     * Targets a room when emitting.
     *
     * @param room -
     * @return BroadcastOperator
     */
    public BroadcastOperator to(String... room) {
        return new BroadcastOperator(this.publishListener, this.broadcastOptions).to(room);
    }

    /**
     * Targets a room when emitting.
     *
     * @param room -
     * @return BroadcastOperator
     */
    public BroadcastOperator in(String... room) {
        return new BroadcastOperator(this.publishListener, this.broadcastOptions).in(room);
    }

    /**
     * Excludes a room when emitting.
     *
     * @param room -
     * @return BroadcastOperator
     */
    public BroadcastOperator except(String... room) {
        return new BroadcastOperator(this.publishListener, this.broadcastOptions).except(room);
    }

    /**
     * Sets a modifier for a subsequent event emission that the event data may be lost if the client is not ready to
     * receive messages (because of network slowness or other issues, or because they’re connected through long polling
     * and is in the middle of a request-response cycle).
     *
     * @return BroadcastOperator
     */
    public BroadcastOperator volatile_() {
        return new BroadcastOperator(this.publishListener, this.broadcastOptions).volatile_();
    }

    /**
     * Sets the compress flag.
     *
     * @param compress - if `true`, compresses the sending data
     * @return BroadcastOperator
     */
    public BroadcastOperator compress(boolean compress) {
        return new BroadcastOperator(this.publishListener, this.broadcastOptions).compress(compress);
    }

    /**
     * Makes the matching socket instances join the specified rooms
     *
     * @param room -
     */
    public void socketsJoin(String... room) {
        new BroadcastOperator(this.publishListener, this.broadcastOptions).socketsJoin(room);
    }

    /**
     * Makes the matching socket instances leave the specified rooms
     *
     * @param room -
     */
    public void socketsLeave(String... room) {
        new BroadcastOperator(this.publishListener, this.broadcastOptions).socketsLeave(room);
    }

    /**
     * Makes the matching socket instances disconnect
     *
     * @param close - whether to close the underlying connection
     */
    public void disconnectSockets(boolean close) {
        new BroadcastOperator(this.publishListener, this.broadcastOptions).disconnectSockets(close);
    }

    /**
     * Send a packet to the Socket.IO servers in the cluster
     *
     * @param args - any number of serializable arguments
     */
    public void serverSideEmit(Object... args) {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("uid", Emitter.UID);
            put("type", RequestType.SERVER_SIDE_EMIT.value);
            put("data", args);
        }};
        try {
            String request = this.broadcastOptions.parser.stringify(map);
            this.publishListener.publish(this.broadcastOptions.requestChannel, request.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            this.broadcastOptions.logger.error("serverSideEmit", exception);
        }
    }
}
