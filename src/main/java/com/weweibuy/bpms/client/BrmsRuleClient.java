package com.weweibuy.bpms.client;

import com.weweibuy.bpms.client.dto.req.RuleExecReqDTO;
import com.weweibuy.framework.common.core.model.dto.CommonDataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author durenhao
 * @date 2020/11/21 10:54
 **/
@FeignClient(name = "brmsRuleClient", contextId = "brmsRuleClient", url = "http://localhost:8050")
@RequestMapping("/rule")
public interface BrmsRuleClient {


    @PostMapping("/exec")
    public CommonDataResponse<Map<String, Object>> execRule(RuleExecReqDTO reqDTO);

}
