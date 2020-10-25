package com.weweibuy.bpms.mq;

import com.weweibuy.bpms.mq.message.TaskCallBackMessage;
import com.weweibuy.framework.rocketmq.annotation.RocketProducer;
import com.weweibuy.framework.rocketmq.annotation.RocketProducerHandler;
import com.weweibuy.framework.rocketmq.annotation.Tag;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * @author durenhao
 * @date 2020/10/25 16:58
 **/
@RocketProducer(topic = "${bpms.mq.task.callback.topic}")
public interface TaskHandlerMqProducer {

    @RocketProducerHandler
    SendResult sendCallBackMessage(TaskCallBackMessage message, @Tag String tag);
}
