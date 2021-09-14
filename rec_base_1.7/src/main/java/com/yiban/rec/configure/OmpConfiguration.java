package com.yiban.rec.configure;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableList;
import com.yiban.rec.configure.internal.AdminFilter;

@Configuration
public class OmpConfiguration {

    @Bean
    public FilterRegistrationBean adminFilterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setUrlPatterns(ImmutableList.of(Constants.ADMIN_PATH_SUFFIX + "/*"));
        bean.setFilter(new AdminFilter());
        return bean;
    }
    
    @Bean
    public RestOperations restTemplate(){
    	return new RestTemplate();
    }
}