package com.yjfshop123.live.server.utils.imageloader;

import java.util.Iterator;
import java.util.Queue;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public interface Deque <E> extends Queue<E> {
    void addFirst(E var1);

    void addLast(E var1);

    boolean offerFirst(E var1);

    boolean offerLast(E var1);

    E removeFirst();

    E removeLast();

    E pollFirst();

    E pollLast();

    E getFirst();

    E getLast();

    E peekFirst();

    E peekLast();

    boolean removeFirstOccurrence(Object var1);

    boolean removeLastOccurrence(Object var1);

    boolean add(E var1);

    boolean offer(E var1);

    E remove();

    E poll();

    E element();

    E peek();

    void push(E var1);

    E pop();

    boolean remove(Object var1);

    boolean contains(Object var1);

    int size();

    Iterator<E> iterator();

    Iterator<E> descendingIterator();
}
