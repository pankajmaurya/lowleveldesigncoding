package dev.lld.practice.blockingqueue;

import java.util.ArrayDeque;

public class BoundedBlockingQUsingWaitNotify<E> implements BoundedBlockingQ<E> {

    private final int capacity;
    private ArrayDeque<E> buffer;
    public BoundedBlockingQUsingWaitNotify(int capacity) {
        this.capacity = capacity;
        this.buffer = new ArrayDeque<E>(this.capacity);
    }

    public void add(E item) throws InterruptedException {

        synchronized (buffer) {
            while(buffer.size() == this.capacity) {
                buffer.wait();
            }

            // we have capacity now..
            buffer.addLast(item);
            System.out.println("Producing " + item);

            buffer.notify();

        }
    }

    public E remove() throws InterruptedException {

        synchronized (buffer) {
            while (buffer.size() == 0) {
                buffer.wait();
            }

            E item = buffer.removeFirst();
            System.out.println("Consumed: " + item);

            buffer.notify();

            return item;
        }
    }
}
