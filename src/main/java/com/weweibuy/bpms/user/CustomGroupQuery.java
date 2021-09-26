package com.weweibuy.bpms.user;

import com.weweibuy.bpms.support.GroupHelper;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.impl.GroupQueryImpl;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.List;

/**
 * @author durenhao
 * @date 2020/10/24 21:50
 **/
public class CustomGroupQuery extends GroupQueryImpl {

    @Override
    public long executeCount(CommandContext commandContext) {
        return GroupHelper.countGroup(this);
    }

    @Override
    public List<Group> executeList(CommandContext commandContext, Page page) {
        return GroupHelper.queryGroup(this);
    }


}
