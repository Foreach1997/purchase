package com.pu.purchase.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;


@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =new MappingJackson2HttpMessageConverter();

        ObjectMapper objectMapper =new ObjectMapper();

        SimpleModule simpleModule =new SimpleModule();

        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);

        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        objectMapper.registerModule(simpleModule);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,true);

        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        jackson2HttpMessageConverter.setObjectMapper(objectMapper);

        converters.add(jackson2HttpMessageConverter);
    }

}
