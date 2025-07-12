package com.weweibuy.bpms.config;

import com.weweibuy.bpms.listener.DynamicTaskCompleteListener;
import com.weweibuy.bpms.listener.DynamicTaskCreateListener;
import com.weweibuy.bpms.listener.ProcessEndListener;
import com.weweibuy.bpms.user.CustomerIdmIdentityServiceImpl;
import com.weweibuy.framework.common.core.utils.IdWorker;
import org.apache.commons.collections.CollectionUtils;
import org.camunda.bpm.engine.impl.cfg.CommandChecker;
import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.multitenancy.TenantCommandChecker;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.bus.CamundaEventBus;
import org.camunda.bpm.extension.reactor.plugin.ReactorProcessEnginePlugin;
import org.camunda.bpm.extension.reactor.projectreactor.EventBus;
import org.camunda.bpm.spring.boot.starter.configuration.CamundaProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        eventBus.register(new DynamicTaskCompleteListener());
        return plugin;
    }

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        // 用户实现替换
        CustomerIdmIdentityServiceImpl customerIdmIdentityService = new CustomerIdmIdentityServiceImpl();
        processEngineConfiguration.setIdentityService(customerIdmIdentityService);

    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        // 支持临时迁移
        allowTempMigration(processEngineConfiguration);
    }

    private void allowTempMigration(ProcessEngineConfigurationImpl processEngineConfiguration) {
        List<CommandChecker> commandCheckers = processEngineConfiguration.getCommandCheckers();
        if (CollectionUtils.isEmpty(commandCheckers)) {
            return;
        }
        List<CommandChecker> collect = commandCheckers.stream()
                .map(c ->
                        c instanceof TenantCommandChecker ? new IgnoreTempMigrationTenantCommandChecker() : c)
                .collect(Collectors.toList());
        processEngineConfiguration.setCommandCheckers(collect);

    }


}
