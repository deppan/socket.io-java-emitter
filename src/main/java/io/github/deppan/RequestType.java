package io.github.deppan;

/**
 * Request types, for messages between nodes
 */
public interface RequestType {
    int SOCKETS = 0;
    int ALL_ROOMS = 1;
    int REMOTE_JOIN = 2;
    int REMOTE_LEAVE = 3;
    int REMOTE_DISCONNECT = 4;
    int REMOTE_FETCH = 5;
    int SERVER_SIDE_EMIT = 6;
}
