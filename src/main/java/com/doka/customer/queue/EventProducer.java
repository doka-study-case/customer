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
    private String routingName;

    @Value("${doka.rabbit.log.exchange}")
    private String exchangeName;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    Gson gson;

    public void send(QueueEvent queueEvent) {
        queueEvent.addParam("log_date", LocalDateTime.now());

        String log = gson.toJson(queueEvent.getLogParams());
        rabbitTemplate.convertAndSend(exchangeName, routingName, log);
    }

}
