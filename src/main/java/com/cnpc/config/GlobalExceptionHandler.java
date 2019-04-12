package com.cnpc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorView(Exception e, HttpServletRequest request) {
        logger.info( "request url = " + request.getRequestURL() );
        ModelAndView mv = new ModelAndView();
        mv.addObject( "exception",e );
        mv.addObject( "url",request.getRequestURL() );
        logger.error( "exception = ",e );
        mv.setViewName( DEFAULT_ERROR_VIEW );
        return mv;
    }
}
