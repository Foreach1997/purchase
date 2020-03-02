package com.pu.purchase.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 供应商/客户信息表
 * </p>
 *
 * @author 
 * @since 2020-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 供应商名称
     */
    private String supplier;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 联系电话
     */
    private String phonenum;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String description;

    /**
     * 启用
     */
    private Boolean enabled;

    /**
     * 地址
     */
    private String address;

    /**
     * 开户行
     */
    @TableField("bankName")
    private String bankName;

    /**
     * 账号
     */
    @TableField("accountNumber")
    private String accountNumber;

    /**
     * 删除标记，0未删除，1删除
     */
    @TableField("delete_Flag")
    private String deleteFlag;

    private BigDecimal score;


}
