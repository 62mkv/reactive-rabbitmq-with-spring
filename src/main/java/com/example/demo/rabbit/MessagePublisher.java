package com.example.demo.rabbit;

import com.example.demo.config.HostProperties;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

import static reactor.rabbitmq.ResourcesSpecification.*;

@Component
public class MessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);

    private final Sender sender;
    private final String exchangeName;
    private final String routingKey;

    public MessagePublisher(ConnectionFactory factory, HostProperties properties) {
        SenderOptions senderOptions = new SenderOptions()
                .connectionFactory(factory)

                .resourceManagementScheduler(Schedulers.elastic());
        this.sender = RabbitFlux.createSender(senderOptions);
        this.exchangeName = properties.getExchangeName();
        this.routingKey = properties.getRoutingKey();
        defineAmqpObjects(properties);
    }

    private void defineAmqpObjects(HostProperties properties) {
        final String exchangeName = properties.getExchangeName();
        final String queueName = properties.getQueueName();
        final String routingKey = properties.getRoutingKey();
        sender.declare(exchange(exchangeName))
                .then(sender.declare(queue(queueName)))
                .then(sender.bind(binding(exchangeName, routingKey, queueName)))
                .subscribe(r -> log.info("Exchange {} and queue {} declared and bound", exchangeName, queueName));
    }

    public void publish(String message) {
        Flux<OutboundMessage> messages = Flux.range(1, 10)
                .map(i -> new OutboundMessage(
                        exchangeName,
                        routingKey, ("Message " + i).getBytes()
                ));

        sender.sendWithPublishConfirms(messages)
                .blockLast();
    }
}
