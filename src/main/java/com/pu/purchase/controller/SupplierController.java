package com.pu.purchase.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pu.purchase.config.BizException;
import com.pu.purchase.dto.SupplierScoreDto;
import com.pu.purchase.entity.*;
import com.pu.purchase.mapper.ExtSupplierScoreMapper;
import com.pu.purchase.mapper.MaterialMapper;
import com.pu.purchase.mapper.SupplierMapper;
import com.pu.purchase.mapper.SupplierScoreMapper;
import com.pu.purchase.service.impl.DeliverFormServiceImpl;
import com.pu.purchase.service.impl.PurchaseDetailServiceImpl;
import com.pu.purchase.service.impl.SupplierServiceImpl;
import com.pu.purchase.util.DateUtils;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.vo.ReqStaticfic;
import com.pu.purchase.vo.SupplierVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/supplier")
public class SupplierController  {


    @Autowired
    private SupplierServiceImpl supplierServiceImpl;

    @Resource
    private MaterialMapper materialMapper;

    @Autowired
    private PurchaseDetailServiceImpl purchaseDetailServiceImpl;

    @Resource
    BlockingQueue<DeliverForm> sendDeliverFormQueue;

    @Autowired
    private DeliverFormServiceImpl deliverFormServiceImpl;

    @Resource
    private SupplierScoreMapper supplierScoreMapper;

    @Resource
    private ExtSupplierScoreMapper extSupplierScoreMapper;

    @Resource
    private SupplierMapper supplierMapper;



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
    @GetMapping("/updateDeliverForm")
    public Object updateDeliverForm(String no,Integer theoryNum,Date theoryTime){
      DeliverForm deliver =  deliverFormServiceImpl.getOne(new QueryWrapper<DeliverForm>().lambda().eq(DeliverForm::getNo,no));
      List<DeliverForm> deliverFormList =   deliverFormServiceImpl.list(new QueryWrapper<DeliverForm>().lambda().eq(DeliverForm::getPurchaseNo,deliver.getPurchaseNo()));
      Integer deliverFormNum = deliverFormList.stream().mapToInt(DeliverForm::getTheoryNum).sum();
      PurchaseDetail purchaseDetail =  purchaseDetailServiceImpl.getOne(new QueryWrapper<PurchaseDetail>().lambda()
                .eq(PurchaseDetail::getPurchaseNo,deliver.getPurchaseNo()));
      int result =  deliverFormNum + theoryNum;
        if (purchaseDetail.getPurchaseQuality().equals(deliverFormNum)){

        }
        if (theoryNum > purchaseDetail.getPurchaseQuality() ){
            throw  new BizException("大于我们采购数量");
        }
        if (theoryNum > purchaseDetail.getPurchaseQuality()){
            throw  new BizException("大于我们采购数量!我们还需要最多:"+ (purchaseDetail.getPurchaseQuality()-deliverFormNum)+",或者"+purchaseDetail.getPurchaseQuality());
        }
        DeliverForm deliverForm = new DeliverForm();
        deliverForm.setTheoryTime(DateUtils.getLocalDateTime(theoryTime));
        deliverForm.setTheoryNum(theoryNum);
        deliverForm.setNo(no);
        deliverForm.setStatus(1);
      Boolean flag =  deliverFormServiceImpl.update(deliverForm,new QueryWrapper<DeliverForm>().lambda()
                .eq(DeliverForm::getNo,deliverForm.getNo())
                .eq(DeliverForm::getStatus,0));
      return RepResult.repResult(0,"成功",flag);
    }

    /**
     * 插入供应商能提供的商品
     */
    @PostMapping("/insertSupplierScore")
    public Object insertSupplierScore(SupplierVo supplierVo){

        Long supplierId = supplierVo.getId();
        Long[] ids =  supplierVo.getIds();
        int supplier_id = supplierScoreMapper.delete(new QueryWrapper<SupplierScore>().eq("supplier_id", supplierId)
        .notIn("material_id",ids));
        if(ids!=null && ids.length>0) {
            for (Long integer : ids) {
                SupplierScore centre = new SupplierScore();
                centre.setSupplierId(supplierId);
                centre.setMaterialId(integer.longValue());
                if (supplierScoreMapper.selectCount(new QueryWrapper<SupplierScore>().eq("supplier_id", supplierId)
                        .eq("material_id",integer.longValue())) == 0){
                    supplierScoreMapper.insert(centre);
                }
            }
        }
       return RepResult.repResult(0,"修改成功", null);
    }

    /**
     * 返回供应商已有商品
     * @param id
     * @return
     */
    @RequestMapping("/getloadSupplier")
    public Object loadSuplier(String id){
        List<Material> materials = materialMapper.selectList(new QueryWrapper<Material>());

        List<SupplierScore> supplierScore = supplierScoreMapper.selectList(new QueryWrapper<SupplierScore>().eq("supplier_id", id));
        List<Map<String,Object>> list = new ArrayList<>();
        for (Material r1 : materials) {
            Boolean LAY_CHECKED=false;
            for (SupplierScore r2 : supplierScore) {
                if(r1.getId().equals(r2.getMaterialId())) {
                    LAY_CHECKED=true;
                    break;
                }
            }
            Map<String,Object> map=new HashMap<>();
            map.put("materialId", r1.getId());
            map.put("name", r1.getName());
            map.put("LAY_CHECKED", LAY_CHECKED);
            list.add(map);
        }
        return RepResult.repResult(0, "查询成功", list);
    }

    /**
     * 选择供应商能提供的商品
     */
    @GetMapping("/selectSupplierScore")
    public Object selectSupplierScore(){
        return RepResult.repResult(0,"", materialMapper.selectList(new QueryWrapper<>()));
    }

    /**
     * 供应商积分
     */
    @GetMapping("/getAllSupplierScore")
    public Object getAllSupplierScore(int size,int current,String materialId,String supplierName,String materName){
        Map<String,Object> map = new HashMap<>();
        map.put("materialId",materialId);
        map.put("supplierName",supplierName);
        map.put("materName",materName);
        Integer page = (current-1)*size;
        map.put("page",page);
        map.put("limit",size);
        System.out.println("page"+size+"limit"+current);
        Integer i = extSupplierScoreMapper.countNum(map);
        List<SupplierScoreDto> supplierScores = extSupplierScoreMapper.querySupplier(map);
        return RepResult.repResult(0,"", supplierScores,i.longValue());
    }
}
