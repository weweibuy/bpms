package com.weweibuy.bpms.config;

import com.weweibuy.framework.common.log.mvc.TraceCodeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author durenhao
 * @date 2022/7/11 23:27
 **/
@Configuration
public class CustomWenConfig {

    @Bean
    public TraceCodeFilter traceCodeFilter() {
        return new TraceCodeFilter();
    }

    @Bean
    public BpmsRequestLogContextFilter requestLogContextFilter() {
        return new BpmsRequestLogContextFilter();
    }



}
