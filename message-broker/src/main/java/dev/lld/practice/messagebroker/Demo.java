package dev.lld.practice.messagebroker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Demo {

    public static class SimplePublisher implements Runnable {

        private final Broker broker;

        public SimplePublisher(Broker broker) {
            this.broker = broker;
        }

        @Override
        public void run() {

            for (int i = 0; i < 10; i++) {
                System.out.println("publishing " + i);
                broker.publishMessage(new Message("message " + i), "topic1");
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            broker.publishMessage(new Message("poison"), "topic1");

        }
    }

    public static class SimpleSubscriber implements Runnable {

        private final Consumer consumer;
        private final String topicName;

        public SimpleSubscriber(Consumer consumer, String topicName) {
            this.consumer = consumer;
            this.topicName = topicName;
            this.consumer.subscribe(topicName);
        }

        @Override
        public void run() {
            while (true) {

                Message message = consumer.consume(topicName);
                System.out.println(message.getPayload());

                if (message.getPayload().equals("poison")) {
                    return;
                }

                System.out.println("Received " + message.getPayload());

            }

        }
    }


    public static void main(String[] args) throws InterruptedException {
        SimpleBroker simpleBroker = new SimpleBroker();
        simpleBroker.createTopic("topic1");

        SimplePublisher simplePublisher = new SimplePublisher(simpleBroker);

        Consumer consumer = new Consumer(simpleBroker);

        SimpleSubscriber simpleSubscriber = new SimpleSubscriber(consumer, "topic1");


        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        executorService.submit(simplePublisher);
        executorService.submit(simpleSubscriber);

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);



    }
}
