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
 * @since 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("supplier_score_flow")
public class SupplierScoreFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private BigDecimal score;

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
