package com.pu.purchase.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseFormDto implements Serializable {

    /**
     * 采购单号
     */
    private String no;

    /**
     * 供应商ID
     */
    private Integer supplierId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态：-1已作废0待确认1已确认2已完成
     */
    private Integer status;

    /**
     * 记录生成人
     */
    private String createPerson;
    /**
     * 记录生成时间
     */
    private LocalDateTime createDate;

    /**
     * 更新人
     */
    private String updatePerson;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 货品编号
     */
    private String productNo;

    /**
     * 采购数量
     */
    private Integer purchaseQuality;

    /**
     * 合格数量
     */
    private Integer qualifiedQuality;

    /**
     * 已入库数量
     */
    private Integer storageQuality;

    /**
     * 采购价格
     */
    private BigDecimal purchasePrice;

}
