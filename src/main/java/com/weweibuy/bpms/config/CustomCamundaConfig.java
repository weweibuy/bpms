package com.weweibuy.bpms.config;

import com.weweibuy.bpms.listener.DynamicTaskCreateListener;
import com.weweibuy.bpms.listener.ProcessEndListener;
import com.weweibuy.bpms.user.CustomerIdmIdentityServiceImpl;
import com.weweibuy.framework.common.core.utils.IdWorker;
import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.bus.CamundaEventBus;
import org.camunda.bpm.extension.reactor.plugin.ReactorProcessEnginePlugin;
import org.camunda.bpm.extension.reactor.projectreactor.EventBus;
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

    @Bean
    public ProcessEnginePlugin reactorPlugin() {
        ReactorProcessEnginePlugin plugin = CamundaReactor.plugin();
        CamundaEventBus eventBus = plugin.getEventBus();
        // 注册
        eventBus.register(new DynamicTaskCreateListener());
        eventBus.register(new ProcessEndListener());
        return plugin;
    }

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        CustomerIdmIdentityServiceImpl customerIdmIdentityService = new CustomerIdmIdentityServiceImpl();
        processEngineConfiguration.setIdentityService(customerIdmIdentityService);
    }


}
