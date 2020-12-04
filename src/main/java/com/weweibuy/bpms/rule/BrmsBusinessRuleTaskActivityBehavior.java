package com.weweibuy.bpms.rule;

import com.weweibuy.bpms.support.BrmsRuleHelper;
import com.weweibuy.framework.common.core.exception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.BusinessRuleTaskActivityBehavior;
import org.flowable.variable.api.persistence.entity.VariableInstance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author durenhao
 * @date 2020/11/21 10:25
 **/
@Slf4j
public class BrmsBusinessRuleTaskActivityBehavior extends BusinessRuleTaskActivityBehavior {


    @Override
    public void execute(DelegateExecution execution) {

        if (rulesExpressions.size() != 1) {
            throw Exceptions.business("只能有且只有一个规则集");
        }

        Expression next = rulesExpressions.iterator().next();
        String ruleSet = next.getValue(execution).toString();
        Map<String, VariableInstance> variableInstances = execution.getVariableInstances();

        Map<String, Object> variableMap = new HashMap<>();
        if (variablesInputExpressions != null) {
            Iterator<Expression> itVariable = variablesInputExpressions.iterator();
            while (itVariable.hasNext()) {
                Expression variable = itVariable.next();
                Object value = variable.getValue(execution);
                buildRuleVariable(variableMap, variable, value);
            }
        }

        Map<String, Object> stringObjectMap = BrmsRuleHelper.execRule(ruleSet, null, variableMap);

        if (MapUtils.isNotEmpty(stringObjectMap)) {
            if (StringUtils.isNotBlank(resultVariable) && !"org.flowable.engine.rules.OUTPUT".equals(resultVariable)) {
                execution.setVariable(resultVariable, stringObjectMap.get(resultVariable));
            } else {
                stringObjectMap.forEach((k, v) -> execution.setVariable(k, v));
            }
        }
        leave(execution);
    }


    /**
     * 构建变量
     * 1. ${user} 形式 key=user value=对应的值
     * 2. name='tom'  形式 key=name value=tom (value 为字符)
     * 3. age=12  形式 key=age value=12 (value为数字)
     * 4. ${user.name}  形式  {user: {name: xxx}}
     *
     * @param variableMap
     * @param expression
     * @param value
     */
    private void buildRuleVariable(Map<String, Object> variableMap, Expression expression, Object value) {
        String expressionText = expression.getExpressionText();
        if (StringUtils.startsWith(expressionText, "${") && expressionText.endsWith("}")) {
            buildExpressionValue(variableMap, expressionText, value);
        } else if (expressionText.indexOf('=') != -1) {
            buildCustomValue(variableMap, expressionText);
        } else {
            variableMap.put(expressionText, value);
        }
    }

    private void buildCustomValue(Map<String, Object> variableMap, String expressionText) {
        String[] strings = expressionText.split("=");
        String name = strings[0];
        String value = strings[1];
        if (value.startsWith("'") && value.endsWith("'")) {
            variableMap.put(name, value.substring(1, value.length() - 1));
        } else if (StringUtils.isNumeric(value)) {
            variableMap.put(name, Long.valueOf(value));
        } else {
            variableMap.put(name, value);
        }
    }


    private void buildExpressionValue(Map<String, Object> variableMap, String expressionText, Object value) {
        String substring = expressionText.substring(2, expressionText.length() - 1);
        if (substring.indexOf('.') != -1) {
            String[] split = substring.split("\\.");
            nextValue(variableMap, split, 0, split.length, value);
        } else {
            variableMap.put(substring, value);
        }
    }

    private void nextValue(Map<String, Object> variableMap, String[] split, int index, int length, Object value) {
        if (index < length - 1) {
            Object o = variableMap.get(split[index]);
            if (o == null) {
                Map<String, Object> map = new HashedMap();
                variableMap.put(split[index], map);
                nextValue(map, split, ++index, length, value);
            } else if (o instanceof Map) {
                nextValue((Map) o, split, ++index, length, value);
            } else {
                String format = String.format("变量, %s 取值冲突, 原值: %s, 现值: %s", Arrays.stream(split).collect(Collectors.joining(".")),
                        o,
                        value);
                log.warn(format);
                throw Exceptions.business(format);
            }
        } else {
            variableMap.put(split[index], value);
        }
    }


}
