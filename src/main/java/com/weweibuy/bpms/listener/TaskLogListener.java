package com.weweibuy.bpms.listener;

import com.weweibuy.bpms.listener.event.CustomTaskEvent;
import groovyjarjarpicocli.CommandLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @date 2025/7/4
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskLogListener implements TaskListener {

    private final ApplicationContext applicationContext;

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        String currentActivityName = delegateTask.getExecution().getCurrentActivityName();
        log.info("任务监听事件  当前节点: {}, 事件名称: {}", currentActivityName, eventName);
        CustomTaskEvent customTaskEvent = new CustomTaskEvent();
        customTaskEvent.setEventName(eventName);
        customTaskEvent.setNodeName(currentActivityName);
        applicationContext.publishEvent(customTaskEvent);
    }

    @TransactionalEventListener
    public void onTaskEvent(CustomTaskEvent event) {
        log.info("任务事务完成, 事件: {}", event);
    }


}
