package com.cnpc.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginParam {
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6,max = 16,message = "密码必须6-16位之间")
    private String password;
}
