package dev.lld.practice.blockingqueue;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;

public class SemaphoreBoundedQueue<E> implements BoundedBlockingQ<E> {

    private final int capacity;
    private ArrayDeque<E> buffer;
    private Semaphore openSlots, filledSlots, queueAccessTicket;

    public SemaphoreBoundedQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Negative capacity provided");
        }
        this.capacity = capacity;
        this.buffer = new ArrayDeque<E>(this.capacity);
        this.openSlots = new Semaphore(this.capacity);
        this.filledSlots = new Semaphore(0);
        this.queueAccessTicket = new Semaphore(1);
    }

    /*
    Approach:
    1. Use raw semaphores with permits tracking capacity (openSlots, filledSlots)
     */
    public void add(E item) throws InterruptedException {
        // need an openSlot
        this.openSlots.acquire();
        this.queueAccessTicket.acquire();
        this.buffer.addLast(item);
        this.queueAccessTicket.release();
        this.filledSlots.release();
    }

    public E remove() throws InterruptedException {
        // need a filled slot
        this.filledSlots.acquire();
        this.queueAccessTicket.acquire();
        E item = this.buffer.removeFirst();
        this.queueAccessTicket.release();
        this.openSlots.release();
        return item;
    }

}
