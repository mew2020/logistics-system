package com.cqcst.filter;

import cn.hutool.json.JSONUtil;
import com.cqcst.common.Result;
import com.cqcst.util.AdminContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "AdminLoginFilter", urlPatterns = "/*")
public class AdminLoginFilter implements Filter {

    //TODO: 登录ThreadLocal存入后get为null
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println("拦截到请求:{" + request.getRequestURI() + "}");

        //判断是否直接放行
        String[] excludingUrls = new String[]{
                "/login",
                "/site",
                "/user",
                "/user/*",
                "/address",
                "/address/*",
                "/order",
                "/order/*",
                "/track",
                "/track/*"
        };
        for (String url : excludingUrls) {
            if (request.getRequestURI().contains(url)) {
                filterChain.doFilter(servletRequest, servletResponse);
                System.out.println("无需阻拦，放行！");
                return;
            }
        }
        if(AdminContext.getAdmin() != null) {
            System.out.println("用户已登录，放行！管理员姓名:" + AdminContext.getAdmin().getName());
            filterChain.doFilter(servletRequest, servletResponse);
        }

        servletResponse.getWriter().write(JSONUtil.toJsonStr(Result.error("NOT LOGIN")));
        return;
    }
}
