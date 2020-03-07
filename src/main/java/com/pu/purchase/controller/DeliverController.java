package com.pu.purchase.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DeliverController {

    /**
     * 跳转至发货单页面
     */
    @RequestMapping("/toDeliverForm")
    public String toDeliverForm() {
        return "DeilMsg/DeilverManager";
    }
}
