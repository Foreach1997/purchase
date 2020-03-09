package com.pu.purchase.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pu.purchase.config.BizException;
import com.pu.purchase.entity.DeliverForm;
import com.pu.purchase.entity.PurchaseDetail;
import com.pu.purchase.entity.Supplier;
import com.pu.purchase.entity.SupplierScore;
import com.pu.purchase.mapper.MaterialMapper;
import com.pu.purchase.mapper.SupplierScoreMapper;
import com.pu.purchase.service.impl.DeliverFormServiceImpl;
import com.pu.purchase.service.impl.PurchaseDetailServiceImpl;
import com.pu.purchase.service.impl.SupplierServiceImpl;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.vo.ReqStaticfic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@RestController
@CrossOrigin
@RequestMapping("/supplier")
public class SupplierController  {


    @Autowired
    private SupplierServiceImpl supplierServiceImpl;

    @Autowired
    private PurchaseDetailServiceImpl purchaseDetailServiceImpl;

    @Autowired
    BlockingQueue<DeliverForm> sendDeliverFormQueue;

    @Autowired
    private DeliverFormServiceImpl deliverFormServiceImpl;

    @Autowired
    private SupplierScoreMapper supplierScoreMapper;

    @Autowired
    private MaterialMapper materialMapper;


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
    public Object updateSupplier(@RequestBody Supplier supplier){
        return RepResult.repResult(0,"成功",supplierServiceImpl.update(supplier,new QueryWrapper<Supplier>().lambda().eq(Supplier::getId,supplier.getId())));
    }
    /**
     * 通用添加供应商
     */
    @PostMapping("/addSupplier")
    public Object addSupplier(@RequestBody Supplier supplier){
        return RepResult.repResult(0,"成功",supplierServiceImpl.save(supplier));
    }

    /**
     * 删除供应商
     */
    @GetMapping("/delSupplier")
    public Object delSupplier(Long id){
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

    /**
     * 选择供应商
     */
    @GetMapping("/choiceSupplier")
    public Object choiceSupplier(int current,int size,Long materialId){
        return   supplierServiceImpl.getAllSupplierScore(current,size,materialId);
    }

    /**
     * 创建发货第一步
     */
    @PostMapping("/addPurchase")
    public Object addPurchase(@RequestBody List<DeliverForm> deliverForms){
        for (DeliverForm deliverForm : deliverForms) {
            deliverForm.setNo("SN"+System.currentTimeMillis());
            deliverFormServiceImpl.save(deliverForm);
        }
        return RepResult.repResult(0,"成功",null);
    }

    /**
     * 创建发货第一步
     */
    @GetMapping("/test")
    public Object test() throws InterruptedException {
        sendDeliverFormQueue.add(new DeliverForm().setDeliverPerson("1231"));
        return sendDeliverFormQueue.take();
    }

    /**
     * 更新deliver_form
     */
    @PostMapping("/updateDeliverForm")
    public Object updateDeliverForm(@RequestBody DeliverForm deliverForm){
        deliverForm.setStatus(1);
      Boolean flag =  deliverFormServiceImpl.update(deliverForm,new QueryWrapper<DeliverForm>().lambda()
                .eq(DeliverForm::getNo,deliverForm.getNo())
                .eq(DeliverForm::getStatus,0));
      return RepResult.repResult(0,"",flag);
    }

    /**
     * 插入供应商能提供的商品
     */
    @PostMapping("/insertSupplierScore")
    public Object insertSupplierScore(@RequestBody SupplierScore supplierScore){
       if (supplierScoreMapper.selectCount(new QueryWrapper<SupplierScore>().lambda()
                .eq(SupplierScore::getMaterialId,supplierScore.getMaterialId()))>0){
           throw  new BizException("当前供应商已经添加过改商品");
       }
       return RepResult.repResult(0,"", supplierScoreMapper.insert(supplierScore));
    }

    /**
     * 选择供应商能提供的商品
     */
    @GetMapping("/selectSupplierScore")
    public Object selectSupplierScore(){
        return RepResult.repResult(0,"", materialMapper.selectList(new QueryWrapper<>()));
    }
}
