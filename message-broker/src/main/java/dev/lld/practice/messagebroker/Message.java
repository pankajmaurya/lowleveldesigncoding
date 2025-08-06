package dev.lld.practice.messagebroker;

import java.util.concurrent.atomic.AtomicLong;

public final class Message {

    private final Object payload;
    private final long id;
    private static AtomicLong idGen = new AtomicLong(1);

    public Message(Object payload) {
        this.payload = payload;
        this.id = idGen.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public Object getPayload() {
        return payload;
    }
}
