package dev.lld.practice.messagebroker;

public class Consumer {

    private final MessageBroker broker;

    public Consumer(MessageBroker messageBroker) {
        this.broker = messageBroker;
    }

    public Message consume(String topicName) {
        // everytime we call consume, we want to pull message from the broker.
        return broker.pullMessage(topicName, this);
    }

    // This is push based style.
    public void onMessageReceived(Message message) {

    }

    public void subscribe(String topicName) {
        broker.subscribe(topicName, this);
    }
}
