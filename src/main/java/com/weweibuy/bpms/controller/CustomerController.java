package com.weweibuy.bpms.controller;

import com.google.j2objc.annotations.ReflectionSupport;
import com.weweibuy.bpms.command.CustomDeleteTaskCommand;
import com.weweibuy.bpms.config.IgnoreTempMigrationTenantCommandChecker;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.TaskServiceImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @date 2025/7/1
 **/
@RequestMapping("/custom")
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final RuntimeService runtimeService;

    private final ProcessEngineConfigurationImpl processEngineConfiguration;

    private final RepositoryService repositoryService;

    /**
     * 多实例 并行加签
     *
     * @param instanceId
     * @param active
     * @param name
     */
    @GetMapping("/addSign")
    public void addSign(String instanceId, String active, String name) {
        runtimeService.createProcessInstanceModification(instanceId)
                .startBeforeActivity(active)
                .setVariable("assignee", name)
                .execute();
    }

    /**
     * 多实例 串行加签
     */
    @PostMapping("/addSignSequential")
    public void addSignSequential(@RequestBody AddSignSequential addSignSequential) {
        runtimeService.createProcessInstanceModification(addSignSequential.getInstanceId())
                .startBeforeActivity(addSignSequential.getActive())
                .setVariable("assignee", addSignSequential.getAssignee())
                .setVariable("nrOfActiveInstances", addSignSequential.getNrOfActiveInstances())
                .setVariable("loopCounter", addSignSequential.getLoopCounter())
                .setVariable("nrOfInstances", addSignSequential.getNrOfInstances())
                .setVariable("nrOfCompletedInstances", addSignSequential.getNrOfCompletedInstances())
                .execute();
    }

    @Data
    public static class AddSignSequential {

        private String instanceId;
        private String active;
        private String assignee;
        private Integer nrOfActiveInstances;
        private Integer loopCounter;
        private Integer nrOfInstances;
        private Integer nrOfCompletedInstances;

    }

    /**
     * 多实例 并行加签
     *
     * @param instanceId
     * @param node
     */
    @GetMapping("/jump")
    public void jump(String instanceId, String node) {
        ActivityInstance activityInstance =
                runtimeService.getActivityInstance(instanceId);

        String activityId = activityInstance.getId();
        runtimeService.createProcessInstanceModification(instanceId)
                .cancelActivityInstance(activityId)
                .cancelAllForActivity(activityId)
                .startBeforeActivity(node)
                .execute();
    }

    @GetMapping("/deleteTask")
    public void deleteTask(String taskId) {
        TaskServiceImpl taskService = (TaskServiceImpl) processEngineConfiguration.getTaskService();
        CommandExecutor commandExecutor = taskService.getCommandExecutor();
        commandExecutor.execute(new CustomDeleteTaskCommand(taskId, "forceDelete"));
    }


    @PostMapping("/tmpDeployment")
    public void tmpDeployment(MultipartFile file, String instanceId) throws Exception {
        byte[] bytes = file.getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // 部署为临时 Deployment（添加唯一标识避免冲突）
        Deployment tempDeployment = repositoryService.createDeployment()
                .addInputStream(file.getOriginalFilename(), byteArrayInputStream)
                .name("TEMP_DEPLOYMENT_" + instanceId) // 唯一名称
                .tenantId(IgnoreTempMigrationTenantCommandChecker.TEMP_TENANT)
                .enableDuplicateFiltering(false)
                .deployWithResult();


        // 获取新流程定义ID
        ProcessDefinition newDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(tempDeployment.getId())
                .singleResult();
    }

    @GetMapping("/tempMigration")
    public void tempMigration(String instanceId) {
        List<Deployment> list = repositoryService.createDeploymentQuery()
                .deploymentName("TEMP_DEPLOYMENT_" + instanceId)
                .orderByDeploymentTime()
                .desc()
                .list();
        Deployment deployment = list.get(0);
        // 获取新流程定义ID
        ProcessDefinition newDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(instanceId).singleResult();

        // 根据流程定义ID查询
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .singleResult();

        MigrationPlan migrationPlan = runtimeService.createMigrationPlan(
                processDefinition.getId(), newDefinition.getId())
                // 自动匹配相同ID的节点
                .mapEqualActivities()
                // 手动映射不同ID的节点
//                        .mapActivities("oldTask", "newTask")
                .build();

        runtimeService.newMigration(migrationPlan)
                .processInstanceIds(Arrays.asList(instanceId))  // 指定迁移的实例
                .skipCustomListeners()     // 可选：跳过监听器
                .skipIoMappings()          // 可选：跳过输入输出映射
                .execute();
    }


}
