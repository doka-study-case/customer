package com.doka.customer.config;

import com.doka.customer.util.LocalDateTimeDeserializer;
import com.doka.customer.util.LocalDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * error: Failed making field 'java.time.LocalDateTime#date' accessible; either increase its visibility or write a custom TypeAdapter for its declaring type<br/>
     * solution: https://www.javaguides.net/2019/11/gson-localdatetime-localdate.html
     */
    @Bean
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());

        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());

        return gsonBuilder.setPrettyPrinting().create();
    }

}
