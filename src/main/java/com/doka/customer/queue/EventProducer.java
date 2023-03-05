package com.doka.customer.queue;

import com.google.gson.Gson;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventProducer {

    @Value("${doka.rabbit.log.routing}")
    private String logRoutingName;

    @Value("${doka.rabbit.log.exchange}")
    private String logExchangeName;

    @Value("${doka.rabbit.transaction.routing}")
    private String transactionRoutingName;

    @Value("${doka.rabbit.transaction.exchange}")
    private String transactionExchangeName;

    @Value("${doka.rabbit.notification.routing}")
    private String notificationRoutingName;

    @Value("${doka.rabbit.notification.exchange}")
    private String notificationExchangeName;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    Gson gson;

    public void sendLog(QueueEvent queueEvent) {
        queueEvent.addParam("log_date", LocalDateTime.now());

        String logJson = gson.toJson(queueEvent.getLogParams());
        rabbitTemplate.convertAndSend(logExchangeName, logRoutingName, logJson);
    }

    public void sendTransaction(QueueTransaction queueTransaction) {
        queueTransaction.setTransactionDate(LocalDateTime.now());

        String transactionJson = gson.toJson(queueTransaction);
        rabbitTemplate.convertAndSend(transactionExchangeName, transactionRoutingName, transactionJson);
    }

    public void sendNotification(QueueNotification queueNotification) {
        queueNotification.setNotificationDate(LocalDateTime.now());

        String notificationJson = gson.toJson(queueNotification);
        rabbitTemplate.convertAndSend(notificationExchangeName, notificationRoutingName, notificationJson);
    }

}
