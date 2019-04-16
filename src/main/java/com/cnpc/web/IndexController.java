package com.cnpc.web;

import com.cnpc.config.WebConfiguration;
import com.cnpc.model.User;
import com.cnpc.param.LoginParam;
import com.cnpc.param.RegisterParam;
import com.cnpc.param.UserParam;
import com.cnpc.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class IndexController {
    private final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private UserRepository repository;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @RequestMapping("/")
    public String index(HttpServletRequest request) {
        String id = (String) request.getSession().getAttribute( WebConfiguration.LOGIN_KEY );
        if (StringUtils.isEmpty( id )) {
            return "login";
        } else {
            return "redirect:/list";
        }
    }

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/login")
    public String login(@Valid LoginParam loginParam, BindingResult result,
                        ModelMap model, HttpServletRequest request) {
        String errorMsg = "";
        if (result.hasErrors()) {
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError error : list) {
                errorMsg = errorMsg + error.getCode() + "-" + error.getDefaultMessage() + ";";
            }
            model.addAttribute( "errorMsg", errorMsg );
            return "login";
        }
        User user = repository.findByUserName( loginParam.getUserName() );
        if (user == null) {
            model.addAttribute( "errorMsg", "用户名不存在,请注册" );
            return "login";
        } else if (!user.getPassword().equals( loginParam.getPassword() )) {
            model.addAttribute( "errorMsg", "密码错误！" );
            return "login";
        }

        request.getSession().setAttribute( WebConfiguration.LOGIN_KEY, user.getId() );
        request.getSession().setAttribute( WebConfiguration.LOGIN_USER, user );
        return "redirect:/list";
    }

    @RequestMapping("/loginOut")
    public String loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute( WebConfiguration.LOGIN_USER );
        request.getSession().removeAttribute( WebConfiguration.LOGIN_KEY );
        return "login";
    }

    @RequestMapping("/toRegister")
    public String toRegister() {
        return "register";
    }

    @RequestMapping("/register")
    public String register(@Valid RegisterParam registerParam, BindingResult result, ModelMap model) {
        logger.info( "register param", registerParam.toString() );
        String errorMsg = "";
        if (result.hasErrors()) {
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError error : list) {
                errorMsg = errorMsg + error.getCode() + "-" + error.getDefaultMessage() + ";";
            }
            model.addAttribute( "errorMsg", errorMsg );
            return "register";
        }
        User user = repository.findByUserNameAndEmail( registerParam.getUserName(), registerParam.getEmail() );
        if (user != null) {
            model.addAttribute( "errorMsg", "用户已存在!" );
            return "register";
        }
        User u = new User();
        BeanUtils.copyProperties( registerParam, u );
        u.setRegTime( new Date() );
        u.setUserType( "manage" );
        u.setState( "unverified" );
        repository.save( u );
        sendRegisterEmail( u );
        logger.info( "register user", u.toString() );
        return "login";
    }

    public void sendRegisterEmail(User user) {
        Context context = new Context();
        context.setVariable( "id",user.getId() );
        String emailContent = templateEngine.process( "emailTemplate",context );
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper( message,true );
            helper.setFrom( from );
            helper.setTo( user.getEmail() );
            helper.setSubject( "注册验证邮件" );
            helper.setText( emailContent,true );
            mailSender.send( message );
        }catch(Exception e){
            logger.error( "发送注册邮件时异常",e );
        }
    }

    @RequestMapping("/verified/{id}")
    public String verified(@PathVariable("id") String id,ModelMap model){
        User user = repository.findById( id ).get();
        if(user != null && "unverified".equals( user.getState() )){
            user.setState( "verified" );
            repository.save( user );
            model.put( "userName",user.getUserName() );
        }
        return "verified";
    }
}
