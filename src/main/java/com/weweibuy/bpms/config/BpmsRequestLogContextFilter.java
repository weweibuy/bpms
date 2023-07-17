//package com.weweibuy.bpms.config;
//
//import com.weweibuy.framework.common.core.model.constant.CommonConstant;
//import com.weweibuy.framework.common.core.utils.HttpRequestUtils;
//import com.weweibuy.framework.common.log.logger.HttpLogger;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.MediaType;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//import org.springframework.web.util.ContentCachingResponseWrapper;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * @author durenhao
// * @date 2020/3/1 10:07
// **/
//@Slf4j
//@Order(100)
//public class BpmsRequestLogContextFilter extends OncePerRequestFilter {
//
//    public static final String  CACHING_REQUEST = "bpms_caching_request";
//
//    @SneakyThrows
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String contentType = request.getContentType();
//        response = new ContentCachingResponseWrapper(response);
//        boolean includePayload = HttpRequestUtils.isIncludePayload(request);
//        if (includePayload) {
//            request = new ContentCachingRequestWrapper(request);
//        }
//        setRequestAttributes(request);
//        // 非json请求
//        if (StringUtils.isBlank(contentType) || !MediaType.valueOf(contentType).isCompatibleWith(MediaType.APPLICATION_JSON) || !includePayload) {
//            HttpLogger.logForNotJsonRequest(request);
//        }
//
//        filterChain.doFilter(request, response);
//        copyAndLogResponse(request, response);
//    }
//
//
//    private void copyAndLogResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        ContentCachingResponseWrapper cachingResponseWrapper = (ContentCachingResponseWrapper) response;
//        InputStream contentInputStream = cachingResponseWrapper.getContentInputStream();
//        String body = IOUtils.toString(contentInputStream, CommonConstant.CharsetConstant.UT8);
//        String contentType = response.getContentType();
//        HttpLogger.logResponseBody(body, response.getStatus());
//        cachingResponseWrapper.copyBodyToResponse();
//    }
//
//
//    private void setRequestAttributes(HttpServletRequest request) {
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        requestAttributes.setAttribute(CommonConstant.HttpServletConstant.REQUEST_METHOD, request.getMethod(), RequestAttributes.SCOPE_REQUEST);
//        requestAttributes.setAttribute(CommonConstant.HttpServletConstant.REQUEST_PATH, request.getRequestURI(), RequestAttributes.SCOPE_REQUEST);
//        requestAttributes.setAttribute(CommonConstant.HttpServletConstant.REQUEST_CONTENT_TYPE, request.getContentType(), RequestAttributes.SCOPE_REQUEST);
//        requestAttributes.setAttribute(CommonConstant.HttpServletConstant.REQUEST_PARAMETER_MAP, request.getParameterMap(), RequestAttributes.SCOPE_REQUEST);
//        requestAttributes.setAttribute(CommonConstant.HttpServletConstant.REQUEST_TIMESTAMP, System.currentTimeMillis(), RequestAttributes.SCOPE_REQUEST);
//        requestAttributes.setAttribute(CommonConstant.HttpServletConstant.REQUEST_TIMESTAMP, System.currentTimeMillis(), RequestAttributes.SCOPE_REQUEST);
//        requestAttributes.setAttribute(CACHING_REQUEST, request, RequestAttributes.SCOPE_REQUEST);
//
//    }
//
//
//
//}
