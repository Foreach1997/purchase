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
 * 
 * </p>
 *
 * @author 
 * @since 2020-03-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("supplier_score")
public class SupplierScore implements Serializable {

    private static final long serialVersionUID = 1L;

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


}
