package com.pu.purchase.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pu.purchase.entity.PurchaseDetail;
import com.pu.purchase.entity.Supplier;
import com.pu.purchase.service.impl.PurchaseDetailServiceImpl;
import com.pu.purchase.service.impl.SupplierServiceImpl;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.vo.ReqStaticfic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/supplier")
public class SupplierController  {


    @Autowired
    private SupplierServiceImpl supplierServiceImpl;

    @Autowired
    private PurchaseDetailServiceImpl purchaseDetailServiceImpl;

    /**
     * 获取全部供应商
     */
    @GetMapping("/getAllSupplier")
    public Object getAllSupplier(int current,int size,String supplier,String phonenum,Integer enabled){

       return supplierServiceImpl.getAllSupplier(current, size, supplier, phonenum, enabled);

    }

    /**
     * 通用更新供应商
     */
    @PostMapping("/updateSupplier")
    public Object updateSupplier(Supplier supplier){
        return RepResult.repResult(0,"成功",supplierServiceImpl.update(supplier,new QueryWrapper<Supplier>().lambda().eq(Supplier::getId,supplier.getId())));
    }


    /**
     * 删除供应商
     */
    @GetMapping("/delSupplier")
    public Object delSupplier(Integer id){
        Supplier supplier = new Supplier();
        supplier.setDeleteFlag("1");
        return RepResult.repResult(0,"成功",supplierServiceImpl.update(supplier,new QueryWrapper<Supplier>().lambda().eq(Supplier::getId,id)));
    }

    /**
     * 获取所有入库统计
     */
    @GetMapping("/getStaticfic")
    public Object getStaticfic(ReqStaticfic reqStaticfic){
        ReqStaticfic r = new ReqStaticfic();
        int count = purchaseDetailServiceImpl.staticficPurchase(r).size();
        return RepResult.repResult(0,"成功",purchaseDetailServiceImpl.staticficPurchase(reqStaticfic), (long) count);
    }

}