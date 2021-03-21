package com.demo.microservice.oauth2server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class GatewayFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayFilter.class);

    private static final String GATEWAY_ENDPOINT_PORT = "8765";
    private static final String X_FORWARDED_PORT = "x-forwarded-port";
    private static final String OAUTH_TOKEN_CHECK_URL = "/oauth/check_token";


    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String proxyForwardedHostHeader = httpServletRequest.getHeader(X_FORWARDED_PORT);

        LOGGER.info(proxyForwardedHostHeader);

        if (httpServletRequest.getRequestURI().equals(OAUTH_TOKEN_CHECK_URL)) {

            //when token validation request comes
            chain.doFilter(request, response);
        } else if (proxyForwardedHostHeader == null || !proxyForwardedHostHeader.equals(GATEWAY_ENDPOINT_PORT)) {

            httpServletResponse.setStatus(401);
            httpServletResponse.getOutputStream().write("Unauthorized".getBytes());
            return;
        }

        chain.doFilter(request, response);
    }
}
