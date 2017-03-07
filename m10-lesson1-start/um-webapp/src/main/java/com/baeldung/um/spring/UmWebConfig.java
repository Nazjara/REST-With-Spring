package com.baeldung.um.spring;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.Filter;

@Configuration
@ComponentScan({ "com.baeldung.common.web", "com.baeldung.um.web" })
@EnableWebMvc
public class UmWebConfig extends WebMvcConfigurerAdapter {

    public UmWebConfig() {
        super();
    }

    // beans

    @Bean
    public FilterRegistrationBean filterRegistration() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(etagFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setName("etagFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public Filter etagFilter() {
        return new ShallowEtagHeaderFilter();
    }

}
