package com.fih.cr.sjm.tico.filter;

import com.amazonaws.util.StringUtils;
import com.fih.cr.sjm.tico.mongodb.documents.Session;
import com.fih.cr.sjm.tico.service.SessionService;
import com.fih.cr.sjm.tico.threadlocal.UserDetailThreadLocal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

public class ThreadLocalPopulateFilter implements Filter {
    private static final String TOKEN_HEADER_NAME = "Authorization";

    private final SessionService sessionService;

    public ThreadLocalPopulateFilter(
            final SessionService sessionService
    ) {
        this.sessionService = sessionService;
    }

    @Override
    public void init(
            final FilterConfig filterConfig
    ) throws ServletException {

    }

    @Override
    public void doFilter(
            final ServletRequest servletRequest,
            final ServletResponse servletResponse,
            final FilterChain filterChain
    ) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

            final String authHeader = httpServletRequest.getHeader(TOKEN_HEADER_NAME);

            try {
                if (!StringUtils.isNullOrEmpty(authHeader)) {
                    final String tokenValue = authHeader.replace("Bearer", "").trim();
                    final Optional<Session> session = this.sessionService.getSessionDetails(tokenValue);
                    session.ifPresent(UserDetailThreadLocal::setUserThreadLocal);
                }
                filterChain.doFilter(servletRequest, servletResponse);
            } finally {
                UserDetailThreadLocal.shredDetails();
            }
        }
    }

    @Override
    public void destroy() {

    }
}
