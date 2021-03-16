package com.alan.developer.rabbitmq;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {

    private final CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(byte[] message) {
        System.out.println(LocalDateTime.now().toString() + ": Received <" + new String(message) + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
