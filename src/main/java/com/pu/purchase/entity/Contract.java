package com.pu.purchase.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 合同表
 * </p>
 *
 * @author 
 * @since 2020-03-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Contract implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 合同编号
     */
    private String no;

    /**
     * 供应商ID
     */
    private Integer supplierId;

    /**
     * 生效开始日期
     */
    private LocalDateTime validateDateBegin;

    /**
     * 生效结束日期
     */
    private LocalDateTime validateDateEnd;

    /**
     * 状态：-1已作废0待审核1已审核
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


}
