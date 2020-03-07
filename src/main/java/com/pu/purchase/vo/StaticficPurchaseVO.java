package com.pu.purchase.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StaticficPurchaseVO implements Serializable {


    private String productNo;

    private Integer purchaseQuality;

    private Integer qualifiedQuality;

    private Integer storageQuality;

    private BigDecimal purchasePrice;

}
