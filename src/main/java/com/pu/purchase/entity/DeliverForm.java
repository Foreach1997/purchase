package com.pu.purchase.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 发货单表
 * </p>
 *
 * @author 
 * @since 2020-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_form")
public class DeliverForm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 发货单号
     */
    private String no;

    /**
     * 采购单号
     */
    private String purchaseNo;

    /**
     * 状态：-1已作废0待确认1已确认2已完成3已退货4已到货
     */
    private Integer status;

    /**
     * 对方发货人
     */
    private String deliverPerson;

    /**
     * 对方发货时间
     */
    private LocalDateTime deliverDate;

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
     * 理论到货时间(供应商)
     */
    private LocalDateTime theoryTime;

    /**
     * 理论数量
     */
    private Integer theoryNum;


}
