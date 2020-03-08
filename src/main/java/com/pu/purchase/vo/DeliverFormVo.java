package com.pu.purchase.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class DeliverFormVo  implements Serializable {

    //分页属性

    private Integer page = 1;
    private Integer limit = 10;

    //产品名称
    private String materialName;

    //理论单价
    private String theoryPrice;

    /**
     * 发货单号
     */
    private String no;

    /**
     * 状态
     */
    private String status;

    /**
     * 采购单号
     */
    private String purchaseNo;

    /**
     * 单价
     */
    private String price;

    /**
     * 数量
     */
    private String num;

    /**
     * 供应商ID
     */
    private String supplierId;

    /**
     * 理论到货时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date theoryTime;

    /**
     * 理论数量
     */
    private String theoryNum;




}
