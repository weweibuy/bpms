package com.weweibuy.bpms.config;

import com.weweibuy.bpms.user.CustomerIdmIdentityServiceImpl;
import com.weweibuy.framework.common.core.utils.IdWorker;
import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.spring.boot.starter.configuration.CamundaProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author durenhao
 * @date 2021/9/23 21:17
 **/
@Configuration
public class CustomCamundaConfig implements CamundaProcessEngineConfiguration {


    @Bean
    public IdGenerator strongUuidGenerator() {
        return () -> IdWorker.nextStringId();
    }

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        CustomerIdmIdentityServiceImpl customerIdmIdentityService = new CustomerIdmIdentityServiceImpl();
        processEngineConfiguration.setIdentityService(customerIdmIdentityService);

    }



}
