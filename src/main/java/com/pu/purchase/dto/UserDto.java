package com.pu.purchase.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDto implements Serializable {

    private String id;

    /**
     * 用户名字
     */
    private String userName;



    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱账号
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

}
