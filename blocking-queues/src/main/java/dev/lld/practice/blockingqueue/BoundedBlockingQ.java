package dev.lld.practice.blockingqueue;

public interface BoundedBlockingQ<E> {
    void add(E item) throws InterruptedException;

    E remove() throws InterruptedException;
}