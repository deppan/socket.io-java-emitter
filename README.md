socket.io-java-emitter
======================


A Java implementation of socket.io-emitter

This project uses [jackson-dataformat-msgpack][mspack-java] and [RedisTemplate]() or [RedisPool]().

### Download

Gradle:
```gradle
dependencies {
  implementation 'io.github.deppan:socket.io-java-emitter:1.0.4'
}
```

Maven:
```xml
<dependency>
  <groupId>io.github.deppan</groupId>
  <artifactId>io-java-emitter</artifactId>
  <version>1.0.4</version>
</dependency>
```

## Usage

### Using with RedisTemplate

```java
RedisClient redisClient = new RedisClient(redisTemplate);
Emitter io = new Emitter(redisClient);
io.emit("event","Hello World!");
```

### Using with JedisPool

```java
RedisClient redisClient = new RedisClient(redisPool);
Emitter io = new Emitter(redisClient);
io.emit("event","Hello World!");
```

## API

### Emitter(client, opts, nsp)

`client` is a wrapper that wraps the redis instance and publishes the message.

The following options are allowed:

- `key`: the name of the key to pub/sub events on as prefix (`socket.io`)
- `parser`: parser to use for encoding messages to Redis ([jackson-dataformat-msgpack][mspack-java])

`nsp`: namespace, default is `"/"`

### Emitter#to(String... room):BroadcastOperator

### Emitter#in(String... room):BroadcastOperator

Specifies a specific `room` that you want to emit to.

### Emitter#except(String... room):BroadcastOperator

Specifies a specific `room` that you want to exclude from broadcasting.

### Emitter#of(String namespace):Emitter

Specifies a specific namespace that you want to emit to.

### Emitter#socketsJoin(String... room)

Makes the matching socket instances join the specified rooms:

```java
// make all Socket instances join the "room1" room
io.socketsJoin("room1");

// make all Socket instances of the "admin" namespace in the "room1" room join the "room2" room
io.of("/admin").in("room1").socketsJoin("room2");
```

### Emitter#socketsLeave(String... room)

Makes the matching socket instances leave the specified rooms:

```java
// make all Socket instances leave the "room1" room
io.socketsLeave("room1");

// make all Socket instances of the "admin" namespace in the "room1" room leave the "room2" room
io.of("/admin").in("room1").socketsLeave("room2");
```

### Emitter#disconnectSockets(boolean close)

Makes the matching socket instances disconnect:

```java
// make all Socket instances disconnect
io.disconnectSockets();

// make all Socket instances of the "admin" namespace in the "room1" room disconnect
io.of("/admin").in("room1").disconnectSockets();

// this also works with a single socket ID
io.of("/admin").in(theSocketId).disconnectSockets();
```

### License

[MIT][MIT]

[Redis]: http://redis.io/

[mspack-java]: https://github.com/msgpack/msgpack-java/blob/main/msgpack-jackson/README.md

[MIT]: http://www.opensource.org/licenses/mit-license.php