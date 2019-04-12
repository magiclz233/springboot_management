package com.cnpc.web;

import com.cnpc.config.WebConfiguration;
import com.cnpc.model.User;
import com.cnpc.param.LoginParam;
import com.cnpc.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

   @Value( "${spring.mail.username}" )
    private String from;

   @RequestMapping("/")
    public String index(HttpServletRequest request){
       String id = (String) request.getSession().getAttribute( WebConfiguration.LOGIN_KEY );
       if(StringUtils.isEmpty( id )){
            return "login";
       }else {
           return "redirect:/list";
       }
   }

   @RequestMapping("/toLogin")
   public String toLogin(){
       return "login";
   }

   @RequestMapping("/login")
   public String login(@Valid LoginParam loginParam, BindingResult result,
                       ModelMap model,HttpServletRequest request){
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
       if(user == null){
           user = repository.findByEmail( loginParam.getUserName() );
       }
       if(user == null){
           model.addAttribute( "errorMsg","用户名不存在" );
           return "login";
       }else if(!user.getPassword().equals( loginParam.getPassword() )){
           model.addAttribute( "errorMsg","密码错误！" );
           return "login";
       }

       request.getSession().setAttribute( WebConfiguration.LOGIN_KEY,user.getId() );
       request.getSession().setAttribute( WebConfiguration.LOGIN_USER,user );
       return "redirect:/list";
   }
}
