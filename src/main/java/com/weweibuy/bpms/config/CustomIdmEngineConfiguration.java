package com.weweibuy.bpms.config;

import com.weweibuy.bpms.user.CustomerIdmIdentityServiceImpl;
import org.flowable.idm.spring.SpringIdmEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

/**
 * @author durenhao
 * @date 2021/8/11 23:57
 **/
@Configuration
public class CustomIdmEngineConfiguration implements EngineConfigurationConfigurer<SpringIdmEngineConfiguration> {

    @Override
    public void configure(SpringIdmEngineConfiguration engineConfiguration) {
        engineConfiguration.setIdmIdentityService(new CustomerIdmIdentityServiceImpl(engineConfiguration));
    }
}
