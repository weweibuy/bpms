package com.weweibuy.bpms.rule;

import com.weweibuy.bpms.support.BrmsRuleHelper;
import com.weweibuy.framework.common.core.exception.Exceptions;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.BusinessRuleTaskActivityBehavior;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author durenhao
 * @date 2020/11/21 10:25
 **/
public class BrmsBusinessRuleTaskActivityBehavior extends BusinessRuleTaskActivityBehavior {


    @Override
    public void execute(DelegateExecution execution) {
        // TODO 调用BRMS

        if (rulesExpressions.size() != 1) {
            throw Exceptions.business("只能有且值有一个规则集");
        }

        Expression next = rulesExpressions.iterator().next();
        String ruleSet = next.getValue(execution).toString();

        Map<String, Object> variableMap = new HashMap<>();
        if (variablesInputExpressions != null) {
            Iterator<Expression> itVariable = variablesInputExpressions.iterator();
            while (itVariable.hasNext()) {
                Expression variable = itVariable.next();
                String expressionText = variable.getExpressionText();
                Object value = variable.getValue(execution);
                variableMap.put(expressionText, value);
            }
        }

        Map<String, Object> stringObjectMap = BrmsRuleHelper.execRule(ruleSet, null, variableMap);

        if (stringObjectMap != null && !stringObjectMap.isEmpty()) {
            execution.setVariable(resultVariable, stringObjectMap.get(resultVariable));
        }
        leave(execution);
    }

}
