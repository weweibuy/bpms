package com.weweibuy.bpms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;

/**
 * @author durenhao
 * @date 2022/7/11 23:27
 **/
@Configuration
public class CustomWenConfig {

    @Bean
    @ConditionalOnMissingBean({ RequestContextListener.class, RequestContextFilter.class })
    @ConditionalOnMissingFilterBean(RequestContextFilter.class)
    public RequestContextFilter requestContextFilter() {
        return new OrderedRequestContextFilter();
    }


}
