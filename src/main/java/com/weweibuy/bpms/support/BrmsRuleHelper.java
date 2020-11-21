package com.weweibuy.bpms.support;

import com.weweibuy.bpms.client.BrmsRuleClient;
import com.weweibuy.bpms.client.dto.req.RuleExecReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author durenhao
 * @date 2020/11/21 10:58
 **/
@Component
public class BrmsRuleHelper {

    private static BrmsRuleClient ruleClient;

    @Autowired
    private BrmsRuleClient brmsRuleClient;

    @PostConstruct
    public void init() {
        ruleClient = brmsRuleClient;
    }

    /**
     * 执行规则
     *
     * @param ruleSet
     * @param agendaGroup
     * @param model
     * @return
     */
    public static Map<String, Object> execRule(String ruleSet, String agendaGroup, Map model) {
        RuleExecReqDTO execReqDTO = new RuleExecReqDTO();
        execReqDTO.setAgendaGroup(agendaGroup);
        execReqDTO.setRuleSetKey(ruleSet);
        execReqDTO.setModel(model);
        return ruleClient.execRule(execReqDTO).getData();
    }

}
