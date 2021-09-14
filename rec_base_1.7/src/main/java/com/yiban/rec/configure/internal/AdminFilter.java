package com.yiban.rec.configure.internal;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.base.Objects;
import com.yiban.rec.configure.Constants;

public class AdminFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        AdminRequest ar = new AdminRequest(request, response);
        if (ar.shouldRedirectToAdminPage()) {
            LOGGER.warn(
                    "Request:{} is admin-sub-request and don't load from admin-main-frame, try to redirect to /admin",
                    ar.getRequestURI());
            ar.redirectToAdminPage();
            return;
        }

        filterChain.doFilter(request, response);
    }
}

class AdminRequest {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private String referer;
    private String requestURI;
    private String adminPath;

    public AdminRequest(ServletRequest request, ServletResponse response) {
        this.request = (HttpServletRequest) request;
        this.response = (HttpServletResponse) response;
        this.referer = this.request.getHeader("referer");
        this.requestURI = this.request.getRequestURI();
        this.adminPath = withContextPath(Constants.ADMIN_PATH_SUFFIX);
    }

    public boolean shouldRedirectToAdminPage() {
        boolean isNotAdminRootRequest = !Objects.equal(adminPath, requestURI);
        boolean isPageRequest = !requestURI.endsWith(".json");
        boolean isDirectAcess = referer == null;
        boolean isRedirectFromLogin = referer != null && referer.contains(Constants.PASSPORT_LOGIN_SUFFIX);
        return isNotAdminRootRequest && isPageRequest && (isDirectAcess || isRedirectFromLogin);
    }

    public void redirectToAdminPage() throws IOException {
        response.sendRedirect(adminPath);
    }

    public String getRequestURI() {
        return this.requestURI;
    }

    private String withContextPath(String path) {
        if (path.startsWith("/")) {
            return request.getContextPath() + path;
        }
        return request.getContextPath() + "/" + path;
    }
}
