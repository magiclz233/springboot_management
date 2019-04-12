package com.cnpc.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class UserParam {
    private String id;

    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6,max = 16,message = "密码必须6-16位之间")
    private String password;

    @Email
    private String email;

    @Max( value = 100,message = "年龄不能大于100岁")
    @Min( value = 18, message = "未成年禁止入内！")
    private Integer age;




}
