package dev.lld.practice.messagebroker;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class NotSoSimpleBroker implements MessageBroker {

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

    public NotSoSimpleBroker() {
        this.deliveryStyle = DeliveryStyle.PUSH;
        this.ackStyle = AckStyle.NO_ACK;
    }

    public NotSoSimpleBroker(DeliveryStyle deliveryStyle, AckStyle ackStyle) {
        this.deliveryStyle = deliveryStyle;
        this.ackStyle = ackStyle;
    }

//    private ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, MappedFileTopic> persistentTopics = new ConcurrentHashMap<>();

    @Override
    public void createTopic(String topicName) {
//        topics.putIfAbsent(topicName, new Topic());
        try {
            persistentTopics.putIfAbsent(topicName, new MappedFileTopic("log-"+topicName));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void publishMessage(Message message, String topicName) {

//        Topic topic = topics.get(topicName);
//
//        if (topic == null) {
//            throw new IllegalArgumentException("Topic not found. Create topic first");
//        }
//
//        topic.publishMessage(message);

        MappedFileTopic mappedFileTopic = persistentTopics.get(topicName);
        if (mappedFileTopic == null) {
            throw new IllegalArgumentException("Topic not found. Create topic first");
        }

        AdvancedMessage advancedMessage = AdvancedMessage.builder().topic(topicName).payload(message.getPayload()).build();
        try {
            mappedFileTopic.publishMessage(advancedMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(String topicName, Consumer consumer) {
//        Topic topic = topics.get(topicName);
//
//        if (topic == null) {
//            throw new IllegalArgumentException("Topic not found. Create topic first");
//        }
//
//        topic.subscribe(consumer);

        MappedFileTopic mappedFileTopic = persistentTopics.get(topicName);
        if (mappedFileTopic == null) {
            throw new IllegalArgumentException("Topic not found. Create topic first");
        }
        mappedFileTopic.subscribe(consumer);
    }

    @Override
    public Message pullMessage(String topicName, Consumer consumer) {
//        Topic topic = topics.get(topicName);
//
//        if (topic == null) {
//            throw new IllegalArgumentException("Topic not found. Create topic first");
//        }
//
//        return topic.readForConsumer(consumer);
        MappedFileTopic mappedFileTopic = persistentTopics.get(topicName);
        if (mappedFileTopic == null) {
            throw new IllegalArgumentException("Topic not found. Create topic first");
        }
        try {
            AdvancedMessage advancedMessage = mappedFileTopic.readForConsumer(consumer);
            return new Message(advancedMessage.getId(), advancedMessage.getPayload());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
