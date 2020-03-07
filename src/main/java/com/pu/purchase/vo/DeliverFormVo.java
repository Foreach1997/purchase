package com.pu.purchase.vo;

import com.pu.purchase.entity.DeliverForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeliverFormVo extends DeliverForm implements Serializable {

    //分页属性

    private Integer page = 1;
    private Integer limit = 10;

    /**
     * 采购单号
     */
    private String purNo;

    /**
     * 供应商ID
     */
    private Integer supplierId;


}
