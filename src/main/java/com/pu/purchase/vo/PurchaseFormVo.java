package com.pu.purchase.vo;


import com.pu.purchase.entity.PurchaseForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class PurchaseFormVo extends PurchaseForm implements Serializable {

    //分页属性

    private Integer page = 1;
    private Integer limit = 10;

    /**
     * 货品编号
     */
    private String productNo;

    /**
     * 采购数量
     */
    private Integer purchaseQuality;

    /**
     * 采购价格
     */
    private BigDecimal purchasePrice;


}
