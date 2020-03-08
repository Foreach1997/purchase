package com.pu.purchase.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeliverFormDto implements Serializable {
    /**
     * 发货单号
     */
    private String no;

    /**
     * 采购单号
     */
    private String purchaseNo;

    /**
     * 状态：-1已作废0待确认1已确认2已完成
     */
    private Integer status;

    /**
     * 对方发货人
     */
    private String deliverPerson;

    /**
     * 对方发货时间
     */
    private String deliverDate;

    /**
     * 记录生成人
     */
    private String createPerson;

    /**
     * 记录生成时间
     */
    private String createDate;

    /**
     * 更新人
     */
    private String updatePerson;

    /**
     * 更新时间
     */
    private String updateDate;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 理论到货时间
     */
    private String theoryTime;

    /**
     * 理论数量
     */
    private Integer theoryNum;
}
