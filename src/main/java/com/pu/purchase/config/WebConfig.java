package com.pu.purchase.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.pu.purchase.entity.DeliverForm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


@Configuration
public class WebConfig  {

    BlockingQueue<Map<String,DeliverForm>> blockingQueue = new ArrayBlockingQueue(100);

    @Bean
    public BlockingQueue<Map<String,DeliverForm>> sendDeliverFormQueue(){
           return blockingQueue;
    }

    @Bean
    public MappingJackson2HttpMessageConverter configureMessageConverters() {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =new MappingJackson2HttpMessageConverter();

        ObjectMapper objectMapper =new ObjectMapper();

        SimpleModule simpleModule =new SimpleModule();

        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);

        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        objectMapper.registerModule(simpleModule);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,true);

        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        jackson2HttpMessageConverter.setObjectMapper(objectMapper);

        return  jackson2HttpMessageConverter;
    }

    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String s) {
                return LocalDateTime.parse( s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

        };
    }
}
