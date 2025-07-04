package com.weweibuy.bpms.command;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.history.UserOperationLogEntry;
import org.camunda.bpm.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.camunda.bpm.engine.impl.cmd.DeleteTaskCmd;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskManager;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.MultiInstanceLoopCharacteristics;

import java.util.Collection;

/**
 * @date 2025/7/4
 **/
public class CustomDeleteTaskCommand extends DeleteTaskCmd {

    private Boolean force;

    public CustomDeleteTaskCommand(String taskId, String deleteReason) {
        this(taskId, deleteReason, false, true);
    }

    public CustomDeleteTaskCommand(String taskId, String deleteReason, boolean cascade) {
        this(taskId, deleteReason, cascade, true);
    }

    public CustomDeleteTaskCommand(String taskId, String deleteReason, boolean cascade, boolean force) {
        super(taskId, deleteReason, cascade);
        this.force = force;
    }

    public CustomDeleteTaskCommand(Collection<String> taskIds, String deleteReason, boolean cascade) {
        this(taskIds, deleteReason, cascade, true);
    }

    public CustomDeleteTaskCommand(Collection<String> taskIds, String deleteReason, boolean cascade, boolean force) {
        super(taskIds, deleteReason, cascade);
        this.force = force;
    }

    @Override
    protected void deleteTask(String taskId, CommandContext commandContext) {
        if (!Boolean.TRUE.equals(force)) {
            super.deleteTask(taskId, commandContext);
            return;
        }

        forceDelete(taskId, commandContext);
    }

    private void forceDelete(String taskId, CommandContext commandContext) {
        TaskManager taskManager = commandContext.getTaskManager();
        TaskEntity task = taskManager.findTaskById(taskId);

        if (task != null) {
            checkDeleteTask(task, commandContext);
            task.logUserOperation(UserOperationLogEntry.OPERATION_TYPE_DELETE);

            forceDelete(taskManager, task);

        } else if (cascade) {
            Context.getCommandContext()
                    .getHistoricTaskInstanceManager()
                    .deleteHistoricTaskInstanceById(taskId);
        }
    }

    private void forceDelete(TaskManager taskManager, TaskEntity task) {
        ExecutionEntity execution = task.getExecution();
        ExecutionEntity parent = execution.getParent();
        if (parent != null) {
            if (isParallelMultiInstance(execution)) {
                Object variableLocal = parent.getVariableLocal(MultiInstanceActivityBehavior.NUMBER_OF_ACTIVE_INSTANCES);
                if (variableLocal instanceof Integer) {
                    parent.setVariableLocal(MultiInstanceActivityBehavior.NUMBER_OF_ACTIVE_INSTANCES,
                            (Integer) variableLocal - 1);
                }
            }
            String reason = (deleteReason == null || deleteReason.length() == 0) ? TaskEntity.DELETE_REASON_DELETED : deleteReason;
            task.delete(reason, cascade);
            execution.remove();
        }

    }

    /**
     * 串行多实例
     *
     * @param execution 子execution
     * @return
     */
    public boolean isSequentialMultiInstance(ExecutionEntity execution) {
        // 并行多实例会有特定的变量
        return execution.getVariableLocal(
                MultiInstanceActivityBehavior.NUMBER_OF_ACTIVE_INSTANCES) != null
                && execution.getVariableLocal(MultiInstanceActivityBehavior.NUMBER_OF_INSTANCES) != null
                && execution.getVariable(MultiInstanceActivityBehavior.NUMBER_OF_COMPLETED_INSTANCES) != null
                && execution.getVariable(MultiInstanceActivityBehavior.LOOP_COUNTER) != null;
    }

    public boolean isParallelMultiInstance(ExecutionEntity execution) {
        return execution.getVariableLocal(MultiInstanceActivityBehavior.LOOP_COUNTER) != null
                && execution.getVariableLocal(MultiInstanceActivityBehavior.NUMBER_OF_ACTIVE_INSTANCES) == null;
    }
}
