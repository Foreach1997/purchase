package com.pu.purchase.controller;


import com.pu.purchase.service.impl.MaterialServiceImpl;
import com.pu.purchase.vo.DeliverFormVo;
import com.pu.purchase.vo.MaterialVo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/mater")
public class MaterialController {

    @Resource
    private MaterialServiceImpl materialService;

    /**
     * 加载商品列表数据
     */
    @GetMapping("/loadAllMaterial")
    public Object loadAllPurchaseForm(MaterialVo materialVo) {
        return materialService.loadAllPurchaseForm(materialVo);
    }

    /**
     * 添加商品
     *
     * @param materialVo
     * @return
     */
    @GetMapping("/addMaterial")
    public Object addMaterial(MaterialVo materialVo) {
        return materialService.insertSelective(materialVo);
    }

    /**
     * 修改商品
     *
     * @param materialVo
     * @return
     */
    @GetMapping("/updateMaterial")
    public Object updateMaterial(MaterialVo materialVo) {
        return materialService.updateByPrimaryKeySelective(materialVo);
    }

    /**
     * 删除商品
     */
    @GetMapping("/deleteMaterial")
    public Object deleteMaterial(String id) {
        return materialService.deleteByPrimaryKey(id);
    }

    /**
     * 加载发货单列表数据
     */
    @GetMapping("/loadAllDeliver")
    public Object loadAllDeliver(DeliverFormVo deliverFormVo) {
        return materialService.loadAllDeliver(deliverFormVo);
    }

    /**
     * 修改发货单状态
     */
    @GetMapping("/updateDeliver")
    public Object updateDeliver(DeliverFormVo deliverFormVo) {
        return materialService.updateDeliver(deliverFormVo);
    }


}
