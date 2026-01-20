package io.github.deppan;

/**
 * Request types, for messages between nodes
 */
public enum RequestType {
    SOCKETS(0),
    ALL_ROOMS(1),
    REMOTE_JOIN(2),
    REMOTE_LEAVE(3),
    REMOTE_DISCONNECT(4),
    REMOTE_FETCH(5),
    SERVER_SIDE_EMIT(6);

    final int value;

    RequestType(int value) {
        this.value = value;
    }
}
