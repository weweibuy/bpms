package com.weweibuy.bpms.client;

import com.weweibuy.brms.api.RuleExecApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author durenhao
 * @date 2020/11/21 10:54
 **/
@FeignClient(name = "brmsRuleClient", contextId = "brmsRuleClient", url = "http://localhost:8050")
public interface BrmsRuleClient extends RuleExecApi {


}
