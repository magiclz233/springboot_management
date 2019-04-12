package com.cnpc.config;

import com.cnpc.web.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SessionFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );
    // 不登陆也可以访问的资源
    private static Set<String> GreenUrlSet = new HashSet<String>();

    //不需要session验证的url
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        GreenUrlSet.add( "/toRegister" );
        GreenUrlSet.add( "/toLogin" );
        GreenUrlSet.add( "/login" );
        GreenUrlSet.add( "/loginOut" );
        GreenUrlSet.add( "/register" );
        GreenUrlSet.add( "/verified" );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        servletResponse.setCharacterEncoding( "UTF-8" );
        servletResponse.setContentType( "text/html;charset=UTF-8" );
        if(requestURI.endsWith( ".js" )
            || requestURI.endsWith( ".css" )
                || requestURI.endsWith( ".jpg" )
                || requestURI.endsWith( ".gif" )
                || requestURI.endsWith( ".png" )
                || requestURI.endsWith( ".ico" )){
            logger.debug( "security filter,pass,"+request.getRequestURI() );
            filterChain.doFilter( servletRequest,servletResponse );
            return;
        }
        System.out.println("request uri = "+requestURI);
        if(GreenUrlSet.contains( requestURI ) || requestURI.contains( "/verified/" )){
            logger.debug( "security filter, pass, " + request.getRequestURI() );
            filterChain.doFilter( servletRequest, servletResponse );
            return;
        }
        String id = (String) request.getSession().getAttribute( WebConfiguration.LOGIN_KEY );
        if(StringUtils.isEmpty( id )){
            String html = "<script type=\"text/javascript\">window.location.href=\"/toLogin\"</script>";
            servletResponse.getWriter().write( html );
        }else {
            filterChain.doFilter( servletRequest,servletResponse );
        }
    }

    @Override
    public void destroy() {

    }
}
