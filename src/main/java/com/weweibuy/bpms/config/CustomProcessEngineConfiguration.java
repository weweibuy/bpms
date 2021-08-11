package com.weweibuy.bpms.config;

import com.weweibuy.framework.common.core.utils.IdWorker;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomProcessEngineConfiguration implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setActivityBehaviorFactory(new ActivityBehaviorWithBrmsFactory());
        engineConfiguration.setIdGenerator(() -> IdWorker.nextStringId());
    }



}