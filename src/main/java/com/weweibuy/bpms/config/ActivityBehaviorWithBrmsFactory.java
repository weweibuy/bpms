package com.weweibuy.bpms.config;

import com.weweibuy.bpms.rule.BrmsBusinessRuleTaskActivityBehavior;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BusinessRuleTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.delegate.BusinessRuleTaskDelegate;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.flowable.engine.impl.delegate.ActivityBehavior;

/**
 * @author durenhao
 * @date 2020/11/21 10:24
 **/
public class ActivityBehaviorWithBrmsFactory extends DefaultActivityBehaviorFactory {

    @Override
    public ActivityBehavior createBusinessRuleTaskActivityBehavior(BusinessRuleTask businessRuleTask) {
        BusinessRuleTaskDelegate ruleActivity = null;
        if (StringUtils.isNotEmpty(businessRuleTask.getClassName())) {
            try {
                Class<?> clazz = Class.forName(businessRuleTask.getClassName());
                ruleActivity = (BusinessRuleTaskDelegate) clazz.newInstance();
            } catch (Exception e) {
                throw new FlowableException("Could not instantiate businessRuleTask (id:" + businessRuleTask.getId() + ") class: " +
                        businessRuleTask.getClassName(), e);
            }
        } else {
            ruleActivity = new BrmsBusinessRuleTaskActivityBehavior();
        }

        for (String ruleVariableInputObject : businessRuleTask.getInputVariables()) {
            ruleActivity.addRuleVariableInputIdExpression(expressionManager.createExpression(ruleVariableInputObject.trim()));
        }

        for (String rule : businessRuleTask.getRuleNames()) {
            ruleActivity.addRuleIdExpression(expressionManager.createExpression(rule.trim()));
        }

        ruleActivity.setExclude(businessRuleTask.isExclude());

        if (businessRuleTask.getResultVariableName() != null && businessRuleTask.getResultVariableName().length() > 0) {
            ruleActivity.setResultVariable(businessRuleTask.getResultVariableName());
        } else {
            ruleActivity.setResultVariable("org.flowable.engine.rules.OUTPUT");
        }

        return ruleActivity;
    }
}
