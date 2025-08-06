package dev.lld.practice.blockingqueue;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQUsingCondVar<E> implements BoundedBlockingQ<E> {
    private final int capacity;
    private ArrayDeque<E> buffer;
    private ReentrantLock lock = new ReentrantLock();
    private Condition notFull, notEmpty;
    public BoundedBlockingQUsingCondVar(int capacity) {
        this.capacity = capacity;
        this.buffer = new ArrayDeque<E>(this.capacity);
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    public void add(E item) throws InterruptedException {
        try {
            lock.lock();

            // Buffer is full.
            while (buffer.size() == this.capacity) {
                notFull.await();
            }

            // buffer not full now.
            this.buffer.addLast(item);
            System.out.println("Producing " + item);


            this.notEmpty.signal();

        } finally {
            lock.unlock();
        }
    }

    public E remove() throws InterruptedException {

        try {
            lock.lock();

            // Buffer is empty
            while (buffer.size() == 0) {
                notEmpty.await();
            }

            // buffer is not notEmpty
            E item = this.buffer.removeFirst();
            System.out.println("Consumed: " + item);

            this.notFull.signal();

            return item;
        } finally {
            lock.unlock();
        }
    }
}
