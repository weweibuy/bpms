package com.weweibuy.bpms.config;

import org.camunda.bpm.engine.impl.cfg.multitenancy.TenantCommandChecker;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.repository.ProcessDefinition;

import java.nio.charset.StandardCharsets;

/**
 * 运行临时性签约, 使用TenantId = temp 存储临时流程
 *
 * @date 2025/7/6
 **/
public class IgnoreTempMigrationTenantCommandChecker extends TenantCommandChecker {

    public static final String TEMP_TENANT = "temp_tenant";


    @Override
    public void checkMigrateProcessInstance(ExecutionEntity processInstance, ProcessDefinition targetProcessDefinition) {
        String tenantId = targetProcessDefinition.getTenantId();
        if (TEMP_TENANT.equals(tenantId)) {
            return;
        }
        super.checkMigrateProcessInstance(processInstance, targetProcessDefinition);
    }
}
