package com.pu.purchase.controller;

import com.pu.purchase.vo.DeliverFormVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/deli")
public class DeliverController {



    /**
     * 跳转至发货单页面
     */
    @RequestMapping("/toDeliverForm")
    public ModelAndView toDeliverForm(String id,String no) {
        ModelAndView modelAndView = new ModelAndView();

        DeliverFormVo deliverFormVo = new DeliverFormVo();
        deliverFormVo.setPurNo(no);
        deliverFormVo.setSupplierId(Integer.parseInt(id));
        modelAndView.setViewName("DeilMag/DeilverManager");
        modelAndView.addObject("deli",deliverFormVo);
        return modelAndView;
    }
}
