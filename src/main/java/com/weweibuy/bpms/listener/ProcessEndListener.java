package com.weweibuy.bpms.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;

// 监听流程结束事件
@Slf4j
@CamundaSelector(type = "endEvent", event = ExecutionListener.EVENTNAME_END)
public class ProcessEndListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        System.out.println("Process ended: " + execution.getProcessDefinitionId());
        log.info("流程结束");
    }
}