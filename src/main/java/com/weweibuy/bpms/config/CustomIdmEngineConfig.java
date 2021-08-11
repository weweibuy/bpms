package com.weweibuy.bpms.config;

import com.weweibuy.bpms.user.CustomerIdmIdentityServiceImpl;
import com.weweibuy.framework.common.core.utils.IdWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.common.spring.AutoDeploymentStrategy;
import org.flowable.common.spring.CommonAutoDeploymentProperties;
import org.flowable.common.spring.SpringEngineConfiguration;
import org.flowable.common.spring.async.SpringAsyncTaskExecutor;
import org.flowable.engine.ProcessEngine;
import org.flowable.http.common.api.client.FlowableHttpClient;
import org.flowable.idm.spring.SpringIdmEngineConfiguration;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.*;
import org.flowable.spring.boot.app.FlowableAppProperties;
import org.flowable.spring.boot.eventregistry.FlowableEventRegistryProperties;
import org.flowable.spring.boot.idm.FlowableIdmProperties;
import org.flowable.spring.boot.process.FlowableProcessProperties;
import org.flowable.spring.boot.process.Process;
import org.flowable.spring.boot.process.ProcessAsync;
import org.flowable.spring.boot.process.ProcessAsyncHistory;
import org.flowable.spring.configurator.DefaultAutoDeploymentStrategy;
import org.flowable.spring.configurator.ResourceParentFolderAutoDeploymentStrategy;
import org.flowable.spring.configurator.SingleResourceAutoDeploymentStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

/**
 * @author durenhao
 * @date 2020/10/23 22:27
 **/
@Slf4j
//@Configuration
@RequiredArgsConstructor
public class CustomIdmEngineConfig {

    private final FlowableProperties flowableProperties;
    protected final FlowableProcessProperties processProperties;
    protected final FlowableAppProperties appProperties;
    protected final FlowableIdmProperties idmProperties;
    protected final FlowableEventRegistryProperties eventProperties;
    protected final FlowableMailProperties mailProperties;
    protected final FlowableHttpProperties httpProperties;
    protected final FlowableAutoDeploymentProperties autoDeploymentProperties;
    private final ResourcePatternResolver resourcePatternResolver;

    @Bean
    public EngineConfigurationConfigurer<SpringIdmEngineConfiguration> customIdmEngineConfigurer() {
        return idmEngineConfiguration -> idmEngineConfiguration
                .setIdmIdentityService(new CustomerIdmIdentityServiceImpl(idmEngineConfiguration));
    }

    protected void configureSpringEngine(SpringEngineConfiguration engineConfiguration, PlatformTransactionManager transactionManager) {
        engineConfiguration.setTransactionManager(transactionManager);
    }

    protected Set<Class<?>> getCustomMybatisMapperClasses(List<String> customMyBatisMappers) {
        Set<Class<?>> mybatisMappers = new HashSet<>();
        for (String customMybatisMapperClassName : customMyBatisMappers) {
            try {
                Class customMybatisClass = Class.forName(customMybatisMapperClassName);
                mybatisMappers.add(customMybatisClass);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Class " + customMybatisMapperClassName + " has not been found.", e);
            }
        }
        return mybatisMappers;
    }

    protected String defaultText(String deploymentName, String defaultName) {
        if (StringUtils.hasText(deploymentName)) {
            return deploymentName;
        }
        return defaultName;
    }

    protected void configureEngine(AbstractEngineConfiguration engineConfiguration, DataSource dataSource) {

        engineConfiguration.setDataSource(dataSource);

        engineConfiguration.setDatabaseSchema(defaultText(flowableProperties.getDatabaseSchema(), engineConfiguration.getDatabaseSchema()));
        engineConfiguration.setDatabaseSchemaUpdate(defaultText(flowableProperties.getDatabaseSchemaUpdate(), engineConfiguration
                .getDatabaseSchemaUpdate()));

        engineConfiguration.setUseLockForDatabaseSchemaUpdate(flowableProperties.isUseLockForDatabaseSchemaUpdate());

        engineConfiguration.setDbHistoryUsed(flowableProperties.isDbHistoryUsed());

        if (flowableProperties.getCustomMybatisMappers() != null) {
            engineConfiguration.setCustomMybatisMappers(getCustomMybatisMapperClasses(flowableProperties.getCustomMybatisMappers()));
        }

        if (flowableProperties.getCustomMybatisXMLMappers() != null) {
            engineConfiguration.setCustomMybatisXMLMappers(new HashSet<>(flowableProperties.getCustomMybatisXMLMappers()));
        }

        if (flowableProperties.getCustomMybatisMappers() != null) {
            engineConfiguration.setCustomMybatisMappers(getCustomMybatisMapperClasses(flowableProperties.getCustomMybatisMappers()));
        }

        if (flowableProperties.getCustomMybatisXMLMappers() != null) {
            engineConfiguration.setCustomMybatisXMLMappers(new HashSet<>(flowableProperties.getCustomMybatisXMLMappers()));
        }

        if (flowableProperties.getLockPollRate() != null) {
            engineConfiguration.setLockPollRate(flowableProperties.getLockPollRate());
        }

        if (flowableProperties.getSchemaLockWaitTime() != null) {
            engineConfiguration.setSchemaLockWaitTime(flowableProperties.getSchemaLockWaitTime());
        }
    }


    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(DataSource dataSource, PlatformTransactionManager platformTransactionManager,
                                                                             @Process ObjectProvider<IdGenerator> processIdGenerator,
                                                                             ObjectProvider<IdGenerator> globalIdGenerator,
                                                                             @ProcessAsync ObjectProvider<AsyncExecutor> asyncExecutorProvider,
                                                                             @Qualifier("applicationTaskExecutor") ObjectProvider<AsyncListenableTaskExecutor> applicationTaskExecutorProvider,
                                                                             @ProcessAsyncHistory ObjectProvider<AsyncExecutor> asyncHistoryExecutorProvider,
                                                                             ObjectProvider<AsyncListenableTaskExecutor> taskExecutor,
                                                                             @Process ObjectProvider<AsyncListenableTaskExecutor> processTaskExecutor,
                                                                             ObjectProvider<FlowableHttpClient> flowableHttpClient,
                                                                             ObjectProvider<List<AutoDeploymentStrategy<ProcessEngine>>> processEngineAutoDeploymentStrategies) throws IOException {

        SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration();

        List<Resource> resources = discoverDeploymentResources(
                flowableProperties.getProcessDefinitionLocationPrefix(),
                flowableProperties.getProcessDefinitionLocationSuffixes(),
                flowableProperties.isCheckProcessDefinitions()
        );

        if (resources != null && !resources.isEmpty()) {
            conf.setDeploymentResources(resources.toArray(new Resource[0]));
            conf.setDeploymentName(flowableProperties.getDeploymentName());
        }

        AsyncExecutor springAsyncExecutor = asyncExecutorProvider.getIfUnique();
        if (springAsyncExecutor != null) {
            conf.setAsyncExecutor(springAsyncExecutor);
        }

        AsyncListenableTaskExecutor asyncTaskExecutor = getIfAvailable(processTaskExecutor, taskExecutor);
        if (asyncTaskExecutor == null) {
            // Get the applicationTaskExecutor
            asyncTaskExecutor = applicationTaskExecutorProvider.getObject();
        }
        if (asyncTaskExecutor != null) {
            // The task executors are shared
            org.flowable.common.engine.api.async.AsyncTaskExecutor flowableTaskExecutor = new SpringAsyncTaskExecutor(asyncTaskExecutor);
            conf.setAsyncTaskExecutor(flowableTaskExecutor);
            conf.setAsyncHistoryTaskExecutor(flowableTaskExecutor);
        }

        AsyncExecutor springAsyncHistoryExecutor = asyncHistoryExecutorProvider.getIfUnique();
        if (springAsyncHistoryExecutor != null) {
            conf.setAsyncHistoryEnabled(true);
            conf.setAsyncHistoryExecutor(springAsyncHistoryExecutor);
        }

        configureSpringEngine(conf, platformTransactionManager);
        configureEngine(conf, dataSource);

        conf.setDeploymentName(defaultText(flowableProperties.getDeploymentName(), conf.getDeploymentName()));

        conf.setDisableIdmEngine(!(flowableProperties.isDbIdentityUsed() && idmProperties.isEnabled()));
        conf.setDisableEventRegistry(!eventProperties.isEnabled());

        conf.setAsyncExecutorActivate(flowableProperties.isAsyncExecutorActivate());
        conf.setAsyncHistoryExecutorActivate(flowableProperties.isAsyncHistoryExecutorActivate());

        conf.setMailServerHost(mailProperties.getHost());
        conf.setMailServerPort(mailProperties.getPort());
        conf.setMailServerSSLPort(mailProperties.getSSLPort());
        conf.setMailServerUsername(mailProperties.getUsername());
        conf.setMailServerPassword(mailProperties.getPassword());
        conf.setMailServerDefaultFrom(mailProperties.getDefaultFrom());
        conf.setMailServerForceTo(mailProperties.getForceTo());
        conf.setMailServerUseSSL(mailProperties.isUseSsl());
        conf.setMailServerUseTLS(mailProperties.isUseTls());

        conf.getHttpClientConfig().setUseSystemProperties(httpProperties.isUseSystemProperties());
        conf.getHttpClientConfig().setConnectionRequestTimeout(httpProperties.getConnectionRequestTimeout());
        conf.getHttpClientConfig().setConnectTimeout(httpProperties.getConnectTimeout());
        conf.getHttpClientConfig().setDisableCertVerify(httpProperties.isDisableCertVerify());
        conf.getHttpClientConfig().setRequestRetryLimit(httpProperties.getRequestRetryLimit());
        conf.getHttpClientConfig().setSocketTimeout(httpProperties.getSocketTimeout());
        conf.getHttpClientConfig().setHttpClient(flowableHttpClient.getIfAvailable());

        conf.setEnableProcessDefinitionHistoryLevel(processProperties.isEnableProcessDefinitionHistoryLevel());
        conf.setProcessDefinitionCacheLimit(processProperties.getDefinitionCacheLimit());
        conf.setEnableSafeBpmnXml(processProperties.isEnableSafeXml());

        conf.setHistoryLevel(flowableProperties.getHistoryLevel());

        conf.setActivityFontName(flowableProperties.getActivityFontName());
        conf.setAnnotationFontName(flowableProperties.getAnnotationFontName());
        conf.setLabelFontName(flowableProperties.getLabelFontName());

        conf.setFormFieldValidationEnabled(flowableProperties.isFormFieldValidationEnabled());

        conf.setEnableHistoryCleaning(flowableProperties.isEnableHistoryCleaning());
        conf.setHistoryCleaningTimeCycleConfig(flowableProperties.getHistoryCleaningCycle());
        conf.setCleanInstancesEndedAfterNumberOfDays(flowableProperties.getHistoryCleaningAfterDays());
        conf.setActivityBehaviorFactory(new ActivityBehaviorWithBrmsFactory());
        conf.setIdGenerator(() -> IdWorker.nextStringId());

        // We cannot use orderedStream since we want to support Boot 1.5 which is on pre 5.x Spring
        List<AutoDeploymentStrategy<ProcessEngine>> deploymentStrategies = processEngineAutoDeploymentStrategies.getIfAvailable();
        if (deploymentStrategies == null) {
            deploymentStrategies = new ArrayList<>();
        }
        CommonAutoDeploymentProperties deploymentProperties = this.autoDeploymentProperties.deploymentPropertiesForEngine(ScopeTypes.BPMN);

        // Always add the out of the box auto deployment strategies as last
        deploymentStrategies.add(new DefaultAutoDeploymentStrategy(deploymentProperties));
        deploymentStrategies.add(new SingleResourceAutoDeploymentStrategy(deploymentProperties));
        deploymentStrategies.add(new ResourceParentFolderAutoDeploymentStrategy(deploymentProperties));
        conf.setDeploymentStrategies(deploymentStrategies);

        return conf;
    }


    public List<Resource> discoverDeploymentResources(String prefix, List<String> suffixes, boolean loadResources) throws IOException {
        if (loadResources) {

            List<Resource> result = new ArrayList<>();
            for (String suffix : suffixes) {
                String path = prefix + suffix;
                Resource[] resources = resourcePatternResolver.getResources(path);
                if (resources != null && resources.length > 0) {
                    Collections.addAll(result, resources);
                }
            }

            if (result.isEmpty()) {
                log.info("No deployment resources were found for autodeployment");
            }

            return result;
        }
        return new ArrayList<>();
    }

    protected <T> T getIfAvailable(ObjectProvider<T> availableProvider, ObjectProvider<T> uniqueProvider) {
        T object = availableProvider.getIfAvailable();
        if (object == null) {
            object = uniqueProvider.getIfUnique();
        }
        return object;
    }


}
