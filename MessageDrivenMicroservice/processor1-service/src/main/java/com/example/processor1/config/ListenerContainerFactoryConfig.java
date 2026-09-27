package com.example.processor1.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerContainerFactoryConfig {

    @Bean("simpleListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory simpleListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Value("${processor1.listeners:3}") int listenerCount) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(listenerCount);
        factory.setMaxConcurrentConsumers(Math.max(listenerCount, 10));
        factory.setPrefetchCount(10);
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.AUTO);
        factory.setDefaultRequeueRejected(false);
        factory.setMessageConverter(new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter());
        return factory;
    }
}
