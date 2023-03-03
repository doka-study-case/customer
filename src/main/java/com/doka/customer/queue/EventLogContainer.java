package com.doka.customer.queue;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class EventLogContainer implements Serializable {

    private Map<String, Object> params = new HashMap<>();

    public void addParam(String key, Object value) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Log key is invalid");
        }

        params.put(key, value != null ? value.toString() : null);
    }

}
