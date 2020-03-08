package com.pu.purchase.vo;

import com.pu.purchase.entity.Material;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MaterialVo extends Material implements Serializable {

    //分页属性

    private Integer page = 1;
    private Integer limit = 10;
}
