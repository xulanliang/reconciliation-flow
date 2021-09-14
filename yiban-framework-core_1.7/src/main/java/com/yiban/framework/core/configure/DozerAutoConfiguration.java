package com.yiban.framework.core.configure;

import java.io.IOException;

import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DozerAutoConfiguration {

    @Autowired
    ApplicationContext ctx;

    @Bean
    public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean() throws IOException {
        DozerBeanMapperFactoryBean ret = new DozerBeanMapperFactoryBean();
        ret.setMappingFiles(ctx.getResources("classpath*:/conf/dozer/*mapping.xml"));

        return ret;
    }
}
