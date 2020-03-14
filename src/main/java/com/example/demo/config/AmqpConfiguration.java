package com.example.demo.config;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Configuration
public class AmqpConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AmqpConfiguration.class);

    @Bean
    public ConnectionFactory connectionFactory(HostProperties properties) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.useNio();

        // TODO: this is just to test configuration
        factory.setHost(UUID.randomUUID().toString());
        return factory.load(buildPropertiesFromSpringProperties(properties.getRabbit()));
    }

    private Properties buildPropertiesFromSpringProperties(Map<String, String> rabbitConfig) {
        Properties properties = new Properties();
        final String prefix = "rabbitmq.";
        for (String key : rabbitConfig.keySet()) {
            properties.setProperty(prefix + key, rabbitConfig.get(key));
        }
        return properties;
    }
}
