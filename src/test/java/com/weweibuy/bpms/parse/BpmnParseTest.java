package com.weweibuy.bpms.parse;

import com.weweibuy.framework.common.core.utils.ClassPathFileUtils;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser;
import org.camunda.bpm.engine.impl.cfg.DefaultBpmnParseFactory;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.el.JuelExpressionManager;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentStatisticsEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.scripting.ScriptFactory;
import org.camunda.bpm.spring.boot.starter.property.Defaults;

import java.io.InputStream;
import java.util.List;

/**
 * @author durenhao
 * @date 2023/7/19 16:36
 **/
public class BpmnParseTest {

    public static void main(String[] args) throws Exception {
        ScriptFactory scriptFactory = new ScriptFactory();
        Defaults.INSTANCE.setScriptFactory(scriptFactory);
        Context.setProcessEngineConfiguration(Defaults.INSTANCE);
        new BpmnParseTest().test();
    }


    public void test() throws Exception {
        DefaultBpmnParseFactory defaultBpmnParseFactory = new DefaultBpmnParseFactory();
        JuelExpressionManager juelExpressionManager = new JuelExpressionManager();

        BpmnParser bpmnParser = new BpmnParser(juelExpressionManager, defaultBpmnParseFactory);

        InputStream inputStream = ClassPathFileUtils.classPathFileOrOther("classpath:event.bpmn");
        DeploymentStatisticsEntity deploymentStatisticsEntity = new DeploymentStatisticsEntity();
        deploymentStatisticsEntity.setName("event_test");

        BpmnParse bpmnParse = bpmnParser
                .createParse()
                .sourceInputStream(inputStream)
                .deployment(deploymentStatisticsEntity);

        bpmnParse.execute();
        List<ProcessDefinitionEntity> processDefinitions = bpmnParse.getProcessDefinitions();
        System.err.println(processDefinitions);

    }


}
