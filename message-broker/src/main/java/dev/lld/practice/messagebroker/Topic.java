package dev.lld.practice.messagebroker;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Topic {

    // To allow subscribers to choose offset, we need an append only log
    // We can simplify this with an in-memory ArrayList which allows random access.

    // If only a single subscriber is there, message can be purged after delivery.
    // Hence we can use an ArrayDeque
    // private ArrayDeque queue = new ArrayDeque();

    // concurrent linked queue will not support offsets.
    // private ConcurrentLinkedQueue q2 = new ConcurrentLinkedQueue();

    private ArrayList<Message> appendOnlyLog = new ArrayList<>();

    private ConcurrentHashMap<Consumer, Integer> consumerToOffsetMap = new ConcurrentHashMap<>();

    public void publishMessage(Message message) {
        synchronized (appendOnlyLog) {
            appendOnlyLog.add(message);
        }
    }

    public void subscribe(Consumer consumer) {
        consumerToOffsetMap.putIfAbsent(consumer, 0);
    }

    public Message readForConsumer(Consumer consumer) {
        int offset = consumerToOffsetMap.get(consumer);
        if (offset < this.appendOnlyLog.size()) {
            consumerToOffsetMap.compute(consumer, (k, v) -> v + 1);
            return this.appendOnlyLog.get(offset);
        }

        return null;
    }
}
