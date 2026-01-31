package io.github.deppan;

public enum PacketType {
    CONNECT(0),
    DISCONNECT(1),
    EVENT(2),
    ACK(3),
    CONNECT_ERROR(4),
    BINARY_EVENT(5),
    BINARY_ACK(6);

    final int value;

    PacketType(int value) {
        this.value = value;
    }
}
