package org.glassfish.jersey.server.internal.inject;

import com.weweibuy.bpms.config.BpmsRequestLogContextFilter;
import com.weweibuy.framework.common.core.utils.HttpRequestUtils;
import com.weweibuy.framework.common.log.logger.HttpLogger;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.Parameter;

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.Parameter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Singleton
class EntityParamValueParamProvider extends AbstractValueParamProvider {

    /**
     * Creates new instance initialized with parameters.
     *
     * @param mpep Injected multivaluedParameterExtractor provider.
     */
    EntityParamValueParamProvider(Provider<MultivaluedParameterExtractorProvider> mpep) {
        super(mpep, Parameter.Source.ENTITY);
    }

    @Override
    protected Function<ContainerRequest, ?> createValueProvider(Parameter parameter) {
        return new EntityValueSupplier(parameter);
    }

    private static class EntityValueSupplier implements Function<ContainerRequest, Object> {

        private final Parameter parameter;

        public EntityValueSupplier(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public Object apply(ContainerRequest containerRequest) {
            try {
                final Class<?> rawType = parameter.getRawType();

                Object value;
                if ((Request.class.isAssignableFrom(rawType) || ContainerRequestContext.class.isAssignableFrom(rawType))
                        && rawType.isInstance(containerRequest)) {
                    value = containerRequest;
                } else {
                    value = containerRequest.readEntity(rawType, parameter.getType(), parameter.getAnnotations());
                    if (rawType.isPrimitive() && value == null) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                                .entity(LocalizationMessages.ERROR_PRIMITIVE_TYPE_NULL()).build());
                    }
                }
                return value;
            } finally {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                HttpServletRequest httpServletRequest = HttpRequestUtils.<HttpServletRequest>getRequestAttribute(requestAttributes, BpmsRequestLogContextFilter.CACHING_REQUEST);
                if (httpServletRequest != null) {
                    HttpLogger.logForJsonRequest(httpServletRequest, true);
                }
            }

        }
    }
}