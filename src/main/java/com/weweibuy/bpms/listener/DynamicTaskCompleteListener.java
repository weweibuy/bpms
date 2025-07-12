package com.weweibuy.bpms.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;

@Slf4j
@CamundaSelector(type = ActivityTypes.TASK_USER_TASK, event = TaskListener.EVENTNAME_COMPLETE)
public class DynamicTaskCompleteListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        // 调用 taskLogListener
        log.info("DynamicTaskCreateListener: 任务结束");
    }
}