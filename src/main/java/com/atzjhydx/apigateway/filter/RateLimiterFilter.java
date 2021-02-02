package com.atzjhydx.apigateway.filter;

import com.atzjhydx.apigateway.exception.RateLimiterException;
import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVLET_DETECTION_FILTER_ORDER;

/**
 * 限流拦截器，使用令牌桶算法
 * @Auther LeeMZ
 * @Date 2021/2/1
 **/
@Component
public class RateLimiterFilter extends ZuulFilter {

    //每秒钟放100个令牌
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(100);

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //这是FilterConstants中定义的最高优先级，-1是为了比这个优先级还要高
        return SERVLET_DETECTION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        if(!RATE_LIMITER.tryAcquire()){
            throw new RateLimiterException(1,"没有获取令牌");
        }
        return null;
    }
}
