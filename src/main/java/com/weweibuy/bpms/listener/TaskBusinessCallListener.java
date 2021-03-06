package com.weweibuy.bpms.listener;

import com.weweibuy.bpms.model.eum.TaskEventTypeEum;
import com.weweibuy.bpms.mq.TaskHandlerMqProducer;
import com.weweibuy.bpms.mq.message.TaskCallBackMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * @author durenhao
 * @date 2020/10/22 22:43
 **/
@Slf4j
@Component("taskBusinessCallListener")
@RequiredArgsConstructor
public class TaskBusinessCallListener implements TaskListener {

    private final RuntimeService runtimeService;

    private final RepositoryService repositoryService;

    private final TaskHandlerMqProducer taskHandlerMqProducer;

    @Override
    public void notify(DelegateTask delegateTask) {
        String processInstanceId = delegateTask.getProcessInstanceId();
        String processDefinitionId = delegateTask.getProcessDefinitionId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();

        // taskDefinitionKey 对应BPMN 的任务主键ID
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String businessKey = processInstance.getBusinessKey();
        /*
         * 这个查出来是空的!!!
         * String processDefinitionKey = processInstance.getProcessDefinitionKey();
         */
        TaskCallBackMessage message = new TaskCallBackMessage();
        message.setBusinessKey(businessKey);
        message.setProcessKey(processDefinition.getKey());
        message.setEventType(TaskEventTypeEum.CREATE);
        message.setTaskKey(taskDefinitionKey);
        taskHandlerMqProducer.sendCallBackMessage(message, "TEST");

        log.error("业务号: {}, 流程key: {}, 任务Key: {} 审批完成", businessKey, processDefinition.getKey(), taskDefinitionKey);
    }
}
