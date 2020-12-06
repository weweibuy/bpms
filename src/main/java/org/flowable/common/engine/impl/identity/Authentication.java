package org.flowable.common.engine.impl.identity;

import com.weweibuy.framework.common.log.support.LogTraceContext;
import org.flowable.common.engine.api.identity.AuthenticationContext;

/**
 * @author durenhao
 * @date 2020/12/5 20:02
 **/
public abstract class Authentication {


    public static void setAuthenticatedUserId(String authenticatedUserId) {
    }

    public static String getAuthenticatedUserId() {
        return LogTraceContext.getUserCode().orElse("0");
    }

    public static AuthenticationContext getAuthenticationContext() {
        return null;
    }

    public static void setAuthenticationContext(AuthenticationContext authenticationContext) {
    }


}
