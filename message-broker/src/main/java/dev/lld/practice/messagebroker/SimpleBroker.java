package dev.lld.practice.messagebroker;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleBroker implements Broker {

    private final DeliveryStyle deliveryStyle;
    private final AckStyle ackStyle;

    enum DeliveryStyle {
        PUSH,
        PULL;
    }

    enum AckStyle {
        NO_ACK,
        ACK;
    }

    public SimpleBroker() {
        this.deliveryStyle = DeliveryStyle.PUSH;
        this.ackStyle = AckStyle.NO_ACK;
    }

    public SimpleBroker(DeliveryStyle deliveryStyle, AckStyle ackStyle) {
        this.deliveryStyle = deliveryStyle;
        this.ackStyle = ackStyle;
    }

    private ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

    @Override
    public void createTopic(String topicName) {
        topics.putIfAbsent(topicName, new Topic());
    }

    @Override
    public void publishMessage(Message message, String topicName) {

        Topic topic = topics.get(topicName);

        if (topic == null) {
            throw new IllegalArgumentException("Topic not found. Create topic first");
        }

        topic.publishMessage(message);

    }

    @Override
    public void subscribe(String topicName, Consumer consumer) {
        Topic topic = topics.get(topicName);

        if (topic == null) {
            throw new IllegalArgumentException("Topic not found. Create topic first");
        }

        topic.subscribe(consumer);
    }

    @Override
    public Message pullMessage(String topicName, Consumer consumer) {
        Topic topic = topics.get(topicName);

        if (topic == null) {
            throw new IllegalArgumentException("Topic not found. Create topic first");
        }

        return topic.readForConsumer(consumer);
    }
}
