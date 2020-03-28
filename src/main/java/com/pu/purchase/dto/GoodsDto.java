package com.pu.purchase.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsDto implements Serializable {


    /**
     * 主键
     */
    private String id;

    /**
     * 名称
     */
    private String name;


    /**
     * 分类名称
     */
    private String goodsIfyName;
}
