package io.kimmking.kmq.core;

import javafx.beans.property.ReadOnlyDoubleProperty;
import lombok.SneakyThrows;

import java.lang.reflect.Array;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class Kmq {

   /* public Kmq(String topic, int capacity) {
        this.topic = topic;
        this.capacity = capacity;
        this.queue = new LinkedBlockingQueue(capacity);
    }*/

    public Kmq(String topic, int capacity) {
        this.topic = topic;
        this.capacity = capacity;
        this.queue =  new MyArrayBlockingQueue(capacity);
    }

    private String topic;

    private int capacity;

    private MyArrayBlockingQueue<KmqMessage> queue;



    public boolean send(KmqMessage message) {
        return queue.put(message);
    }

    public KmqMessage poll() {
        return queue.take();
    }



}
