package com.pu.purchase.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SupplierVo  implements Serializable {

    /**
     * 供应商ID
     */
    private Long id;

    private Integer[] ids;
}
