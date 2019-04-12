package com.cnpc.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {
    public static final long serialVersionUID = 1L;
    private String id;
    private String userName;
    private String userType;
    private String password;
    private String email;
    private Integer age;
    private Date regTime;
    private String state;
        
        

        
        
        

}
