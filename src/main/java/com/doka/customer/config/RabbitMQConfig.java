package com.doka.customer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${doka.rabbit.log.queue}")
    private String logQueueName;

    @Value("${doka.rabbit.log.exchange}")
    private String logExchangeName;

    @Value("${doka.rabbit.log.routing}")
    private String logRoutingName;

    @Value("${doka.rabbit.transaction.queue}")
    private String transactionQueueName;

    @Value("${doka.rabbit.transaction.exchange}")
    private String transactionExchangeName;

    @Value("${doka.rabbit.transaction.routing}")
    private String transactionRoutingName;

    @Value("${doka.rabbit.notification.queue}")
    private String notificationQueueName;

    @Value("${doka.rabbit.notification.exchange}")
    private String notificationExchangeName;

    @Value("${doka.rabbit.notification.routing}")
    private String notificationRoutingName;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    @Qualifier("log")
    public Queue logQueue() {
        return new Queue(logQueueName);
    }

    @Bean
    @Qualifier("transaction")
    public Queue transactionQueue() {
        return new Queue(transactionQueueName);
    }

    @Bean
    @Qualifier("notification")
    public Queue notificationQueue() {
        return new Queue(notificationQueueName);
    }

    @Bean
    @Qualifier("log")
    public DirectExchange logExchange() {
        return new DirectExchange(logExchangeName);
    }

    @Bean
    @Qualifier("transaction")
    public DirectExchange transactionExchange() {
        return new DirectExchange(transactionExchangeName);
    }

    @Bean
    @Qualifier("notification")
    public DirectExchange notificationExchange() {
        return new DirectExchange(notificationExchangeName);
    }

    @Bean
    public Binding logBinding(final @Qualifier("log") Queue queue,
                              final @Qualifier("log") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(logRoutingName);
    }

    @Bean
    public Binding transactionBinding(final @Qualifier("transaction") Queue queue,
                                      final @Qualifier("transaction") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(transactionRoutingName);
    }

    @Bean
    public Binding notificationBinding(final @Qualifier("notification") Queue queue,
                                       final @Qualifier("notification") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(notificationRoutingName);
    }

}
