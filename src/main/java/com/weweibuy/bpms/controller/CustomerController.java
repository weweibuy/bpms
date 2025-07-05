package com.weweibuy.bpms.controller;

import com.google.j2objc.annotations.ReflectionSupport;
import com.weweibuy.bpms.command.CustomDeleteTaskCommand;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.TaskServiceImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.springframework.web.bind.annotation.*;

/**
 * @date 2025/7/1
 **/
@RequestMapping("/custom")
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final RuntimeService runtimeService;

    private final ProcessEngineConfigurationImpl processEngineConfiguration;

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

    @GetMapping("/createTask")
    public void createTask(String instanceId) {

        ActivityInstance activityInstance = runtimeService.getActivityInstance(instanceId);
        String activityId = activityInstance.getActivityId();
        String[] executionIds = activityInstance.getExecutionIds();
        ExecutionEntity rootExecutionEntity = (ExecutionEntity) runtimeService
                .createExecutionQuery()
                .processInstanceId(instanceId)
                .singleResult();
//        rootExecutionEntity.executeActivity(activityInstance);
    }

}
