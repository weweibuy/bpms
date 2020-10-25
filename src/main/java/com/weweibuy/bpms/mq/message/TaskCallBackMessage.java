package com.weweibuy.bpms.mq.message;

import com.weweibuy.bpms.model.eum.TaskEventTypeEum;
import lombok.Data;

import java.util.Map;

/**
 * @author durenhao
 * @date 2020/10/25 17:27
 **/
@Data
public class TaskCallBackMessage {

    private String businessKey;

    private String processKey;

    private String taskKey;

    private TaskEventTypeEum eventType;

    private Map<String, Object> variables;

}
