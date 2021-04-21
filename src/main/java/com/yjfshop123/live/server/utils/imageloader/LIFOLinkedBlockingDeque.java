package com.yjfshop123.live.server.utils.imageloader;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public class LIFOLinkedBlockingDeque <T> extends LinkedBlockingDeque<T> {
    private static final long serialVersionUID = -4114786347960826192L;

    public LIFOLinkedBlockingDeque() {
    }

    public boolean offer(T e) {
        return super.offerFirst(e);
    }

    public T remove() {
        return super.removeFirst();
    }
}
