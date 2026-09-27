package com.example.processor1.config;

import java.util.concurrent.Executor;

import com.example.common.config.QueueNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public DirectExchange processorExchange() {
        return new DirectExchange(QueueNames.EXCHANGE, true, false);
    }

    @Bean
    public Queue q1() {
        return new Queue(QueueNames.Q1, true);
    }

    @Bean
    public Queue q2() {
        return new Queue(QueueNames.Q2, true);
    }

    @Bean
    public Binding q1Binding() {
        return BindingBuilder.bind(q1()).to(processorExchange()).with(QueueNames.Q1);
    }

    @Bean
    public Binding q2Binding() {
        return BindingBuilder.bind(q2()).to(processorExchange()).with(QueueNames.Q2);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Executor asyncExecutor(@Value("${processor1.thread-pool-core-size:8}") int coreSize,
                                 @Value("${processor1.thread-pool-max-size:16}") int maxSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("processor1-");
        executor.initialize();
        return executor;
    }
}
