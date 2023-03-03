package com.doka.customer.queue;

import java.io.Serializable;
import java.util.Map;

public class QueueEvent implements Serializable {

    private final EventLogContainer eventLogContainer;

    public QueueEvent() {
        eventLogContainer = new EventLogContainer();
    }

    public QueueEvent addParam(String key, Object value) {
        eventLogContainer.addParam(key, value);
        return this;
    }

    public QueueEvent setMessage(String message) {
        eventLogContainer.addParam("message", message);
        return this;
    }

    public Map<String, Object> getLogParams() {
        return eventLogContainer.getParams();
    }

}
