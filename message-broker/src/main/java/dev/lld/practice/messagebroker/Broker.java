package dev.lld.practice.messagebroker;

public interface Broker {

    void createTopic(String topicName);

    void publishMessage(Message message, String topicName);

    void subscribe(String topicName, Consumer subscriber);

    Message pullMessage(String topicName, Consumer consumer);
}
