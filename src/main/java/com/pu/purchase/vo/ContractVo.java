package com.pu.purchase.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ContractVo implements Serializable {

    /**
     * 合同编号
     */
    private String no;

    /**
     * 采购单号
     */
    private String purchaseNo;

    /**
     * 数量
     */
    private String num;

    /**
     * 单价
     */
    private String unitPrice;

    /**
     * 单价
     */
    private String price;

    /**
     * 生效开始日期
     */
    private String validateDateBegin;

    /**
     * 生效结束日期
     */
    private String validateDateEnd;

    /**
     * 记录生成人
     */
    private String createPerson;
}
