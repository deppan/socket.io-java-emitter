package io.github.deppan;

public interface PacketType {
    int CONNECT = 0;
    int DISCONNECT = 1;
    int EVENT = 2;
    int ACK = 3;
    int CONNECT_ERROR = 4;
    int BINARY_EVENT = 5;
    int BINARY_ACK = 6;
}
