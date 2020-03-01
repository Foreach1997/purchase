package com.pu.purchase.vo;


import com.pu.purchase.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserVo extends User implements Serializable {

    //分页属性

    private Integer page = 1;
    private Integer limit = 10;
}
