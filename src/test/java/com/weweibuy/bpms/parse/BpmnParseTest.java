package com.weweibuy.bpms.parse;

import com.weweibuy.framework.common.core.utils.ClassPathFileUtils;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser;
import org.camunda.bpm.engine.impl.cfg.DefaultBpmnParseFactory;
import org.camunda.bpm.engine.impl.el.JuelExpressionManager;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentStatisticsEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;

import java.io.InputStream;
import java.util.List;

/**
 * @author durenhao
 * @date 2023/7/19 16:36
 **/
public class BpmnParseTest {

    public static void main(String[] args) throws Exception {
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
