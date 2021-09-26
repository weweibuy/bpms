package com.weweibuy.bpms.user;

import com.weweibuy.bpms.support.UserHelper;
import com.weweibuy.framework.common.core.exception.Exceptions;
import org.camunda.bpm.engine.identity.NativeUserQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.List;

/**
 * @author durenhao
 * @date 2020/10/23 22:50
 **/
public class CustomUserQuery extends UserQueryImpl implements NativeUserQuery {

    @Override
    public long executeCount(CommandContext commandContext) {
        return UserHelper.countUser(this);
    }

    @Override
    public List<User> executeList(CommandContext commandContext, Page page) {
        return UserHelper.queryUser(this);
    }

    @Override
    public NativeUserQuery sql(String selectClause) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public NativeUserQuery parameter(String name, Object value) {
        throw Exceptions.business("不支持用户操作相关功能");
    }
}
