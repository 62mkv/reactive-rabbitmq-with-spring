package com.example.demo.rabbit;

import com.example.demo.config.HostProperties;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.ReceiverOptions;

@Component
public class MessageListener {
    private final Flux<Delivery> receiver;

    public MessageListener(ConnectionFactory factory, HostProperties properties) {
        ReceiverOptions receiverOptions = new ReceiverOptions()
                .connectionFactory(factory)
                .connectionSubscriptionScheduler(Schedulers.elastic());

        this.receiver = RabbitFlux.createReceiver(receiverOptions).consumeAutoAck(properties.getQueueName());
    }

    public Flux<Delivery> getReceiver() {
        return receiver;
    }
}
