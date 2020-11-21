package com.weweibuy.bpms.config;

import org.flowable.engine.impl.bpmn.parser.factory.AbstractBehaviorFactory;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.flowable.spring.SpringProcessEngineConfiguration;

/**
 * @author durenhao
 * @date 2020/11/21 10:21
 **/
public class SpringProcessEngineWithBrmsConfiguration extends SpringProcessEngineConfiguration {


    @Override
    public void initBehaviorFactory() {
        if (activityBehaviorFactory == null) {
            DefaultActivityBehaviorFactory defaultActivityBehaviorFactory = new ActivityBehaviorWithBrmsFactory();
            defaultActivityBehaviorFactory.setExpressionManager(expressionManager);
            activityBehaviorFactory = defaultActivityBehaviorFactory;
        } else if ((activityBehaviorFactory instanceof AbstractBehaviorFactory) && ((AbstractBehaviorFactory) activityBehaviorFactory).getExpressionManager() == null) {
            ((AbstractBehaviorFactory) activityBehaviorFactory).setExpressionManager(expressionManager);
        }
    }
}
