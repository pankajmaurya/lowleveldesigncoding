package dev.lld.practice.messagebroker;

public class Consumer {

    private final Broker broker;

    public Consumer(Broker broker) {
        this.broker = broker;
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
