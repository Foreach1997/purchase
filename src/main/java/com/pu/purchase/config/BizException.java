package com.pu.purchase.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BizException extends RuntimeException {

    private String msg;

    public BizException(String msg) {
        super(msg);
        this.msg = msg;
    }

}
