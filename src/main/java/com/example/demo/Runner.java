package com.example.demo;

import com.example.demo.rabbit.MessageListener;
import com.example.demo.rabbit.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@Component
public class Runner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);
    private final MessagePublisher publisher;
    private final MessageListener listener;

    public Runner(MessagePublisher publisher, MessageListener listener) {
        this.publisher = publisher;
        this.listener = listener;
    }

    @Override
    public void run(String... args) {
        log.info("I am being run");
        publisher.publish("Hello from RabbitMQ");
        log.info("Messages are published");

        listener.getReceiver()
                .doOnNext(delivery -> log.info("Message is being received: {}", delivery.getBody()))
                .blockLast(Duration.of(10L, ChronoUnit.SECONDS));
    }
}
