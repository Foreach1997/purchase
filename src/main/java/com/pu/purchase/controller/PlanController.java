package com.pu.purchase.controller;


import com.pu.purchase.service.impl.MaterialServiceImpl;
import com.pu.purchase.service.impl.PurchaseFormServiceImpl;
import com.pu.purchase.service.impl.SupplierServiceImpl;
import com.pu.purchase.vo.PurchaseFormVo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/plan")
public class PlanController {

    @Resource
    private PurchaseFormServiceImpl purchaseFormServiceImpl;

    @Resource
    private SupplierServiceImpl supplierServiceImpl;


    @Resource
    private MaterialServiceImpl materialService;


    /**
     * 加载采购计划列表数据
     */
    @GetMapping("/loadAllPurchaseForm")
    public Object loadAllPurchaseForm(PurchaseFormVo purchaseFormVo) {
        return purchaseFormServiceImpl.queryAllPurchaseForm(purchaseFormVo);
    }


    /**
     * 添加采购计划
     *
     * @param purchaseFormVo
     * @return
     */
    @GetMapping("/addPurchaseForm")
    public Object addPurchaseForm(PurchaseFormVo purchaseFormVo) {
        return purchaseFormServiceImpl.insertSelective(purchaseFormVo);
    }

    /**
     * 修改采购计划
     *
     * @param purchaseFormVo
     * @return
     */
    @GetMapping("/updatePurchaseForm")
    public Object updatePurchaseForm(PurchaseFormVo purchaseFormVo) {
        return purchaseFormServiceImpl.updateByPrimaryKeySelective(purchaseFormVo);
    }

    /**
     * 删除采购计划
     */
    @GetMapping("/deletePurchaseForm")
    public Object deletePurchaseForm(String id) {
        return purchaseFormServiceImpl.deleteByPrimaryKey(id);
    }


    /**
     * 加载供应商列表
     */
    @GetMapping("/supplier")
    public Object getSupplierList() {
        return supplierServiceImpl.getSupplierList();
    }

    /**
     * 加载产品列表
     */
    @GetMapping("/material")
    public Object getMaterialList() {
        return materialService.getMaterialList();
    }



}
