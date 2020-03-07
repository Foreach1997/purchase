package com.pu.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 采购单表
 * </p>
 *
 * @author 
 * @since 2020-03-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("purchase_form")
public class PurchaseForm implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 采购类型：1经销、2代销、3联营、4包销、5对方发货
     */
    private Integer purchaseType;

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
     * 更新时间
     */
    private LocalDateTime updateDate;


}
