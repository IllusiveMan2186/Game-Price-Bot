package com.gpb.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
public class SessionConfiguration {

    //@Bean(name="entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory() {

        return new LocalSessionFactoryBean();
    }
}
