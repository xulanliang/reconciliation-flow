package com.yiban.framework.core.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

/**
* @author swing
* @date 2018年1月9日 下午5:48:17
* 类说明
*/
@Configuration
@EnableReactor
public class ReactorConfiguration {

    @Bean
    public Reactor reactor(Environment env) {
        return Reactors.reactor().env(env).get();
    }
}