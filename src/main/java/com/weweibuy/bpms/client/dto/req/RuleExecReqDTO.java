package com.weweibuy.bpms.client.dto.req;

import lombok.Data;

import java.util.Map;

/**
 * @author durenhao
 * @date 2020/11/8 20:30
 **/
@Data
public class RuleExecReqDTO {

    private String ruleSetKey;

    private String agendaGroup;

    private Map model;

}
