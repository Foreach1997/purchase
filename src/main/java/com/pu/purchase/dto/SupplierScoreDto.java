package com.pu.purchase.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SupplierScoreDto {

    private Long id;

    /**
     * 积分
     */
    private BigDecimal supplierScore;

    private LocalDateTime createTime;

    /**
     * 货品id
     */
    private Long materialId;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 货品名称
     */
    private String materName;

    /**
     * 供应商名称
     */
    private String  supplierName;

}
