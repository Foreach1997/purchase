package com.pu.purchase.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageUtil implements Serializable {

    private Integer currentPage;

    private Integer pageSize;
}
