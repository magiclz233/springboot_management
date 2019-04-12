package com.cnpc.web;

import com.cnpc.model.User;
import com.cnpc.param.UserParam;
import com.cnpc.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 用户增删改查操作
 *
 * author：luozhang_magic
 */
@Controller
public class UserController {

    public final Logger logger = LoggerFactory.getLogger( this.getClass() );
    @Autowired
    private UserRepository repository;

    @RequestMapping("/list")
    @Cacheable("user_list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0")
            Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Sort sort = new Sort( Sort.Direction.DESC, "id" );
        Pageable pageable = PageRequest.of( page, size, sort );
        Page<User> users = repository.findAll( pageable );
        model.addAttribute( "users", users );
        logger.info( "user list" + users.getContent() );
        return "user/list";
    }

    @RequestMapping("/toAdd")
    public String toAdd() {
        return "user/userAdd";
    }

    @RequestMapping("/add")
    public String add(@Valid UserParam userParam, BindingResult result, ModelMap model) {
        String errorMsg = "";
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            for (ObjectError e :
                    errors) {
                errorMsg = errorMsg + e.getCode() + "-" + e.getDefaultMessage() + ";";
            }
            model.addAttribute( "errorMsg", errorMsg );
            return "user/userAdd";
        }
        User u = repository.findByUserNameAndEmail( userParam.getUserName(), userParam.getEmail() );
        if (u != null) {
            model.addAttribute( "errorMsg", "用户已存在" );
            return "user/userAdd";
        }
        User user = new User();
        BeanUtils.copyProperties( userParam, user );
        user.setRegTime( new Date() );
        user.setUserType( "user" );
        repository.save( user );
        return "redirect:/list";
    }

    @RequestMapping("/toEdit")
    public String toEdit(Model model, String id) {
        User user = repository.findById( id ).get();
        model.addAttribute( "user", user );
        return "user/userEdit";
    }

    @RequestMapping("/edit")
    public String edit(@Valid UserParam userParam, BindingResult result, ModelMap model) {
        String errorMsg = "";
        if (result.hasErrors()) {
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError error : list) {
                errorMsg = errorMsg + error.getCode() + "-" + error.getDefaultMessage() + ";";
            }
            model.addAttribute( "errorMsg", errorMsg );
            model.addAttribute( "user", userParam );
            return "user/userEdit";
        }
        User user = repository.findById( userParam.getId() ).get();
        BeanUtils.copyProperties( userParam, user );
        user.setRegTime( new Date() );
        repository.save( user );
        return "redirect:/list";
    }

    @RequestMapping("/delete")
    public String delete(String id) {
        repository.deleteById( id );
        return "redirect:/list";
    }
}
