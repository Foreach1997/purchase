package com.pu.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.pu.purchase.entity.DeliverForm;
import com.pu.purchase.entity.PurchaseDetail;
import com.pu.purchase.entity.Supplier;
import com.pu.purchase.entity.SupplierScore;
import com.pu.purchase.mapper.DeliverFormMapper;
import com.pu.purchase.mapper.PurchaseDetailMapper;
import com.pu.purchase.mapper.SupplierMapper;
import com.pu.purchase.mapper.SupplierScoreMapper;
import com.pu.purchase.service.ISupplierService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.util.SendEmail;
import org.hibernate.validator.constraints.pl.REGON;
import com.pu.purchase.util.RepResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 供应商/客户信息表 服务实现类
 * </p>
 *
 * @author
 * @since 2020-03-01
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

    @Resource
    private SupplierMapper supplierMapper;
    @Resource
    private PurchaseDetailMapper purchaseDetailMapper;
    @Resource
    private DeliverFormMapper deliverFormMapper;
    @Resource
    private SupplierScoreMapper supplierScoreMapper;


   @Override
   public  Object getAllSupplier(int current, int size, String supplier, String phonenum, Integer enabled){
             IPage<Supplier> supplierIPage = new Page<>();
             supplierIPage.setSize(size);
             supplierIPage.setCurrent(current);
             supplierIPage = supplierMapper.selectPage(supplierIPage,new QueryWrapper<Supplier>().lambda()
                     .like(supplier != null,Supplier::getSupplier,supplier)
                     .like(phonenum != null,Supplier::getPhonenum,phonenum)
                     .eq(enabled != null,Supplier::getEnabled,enabled)
                     .eq(Supplier::getDeleteFlag,"0"));
             return RepResult.repResult(0,"成功",supplierIPage.getRecords(),supplierIPage.getTotal());
    }

    public Object getSupplierList(){
        List<Supplier> supplierList = supplierMapper.selectList(new QueryWrapper<Supplier>().eq("delete_flag", "0"));
        return RepResult.repResult(0, "查询成功", supplierList, (long) supplierList.size());
    }

    public Object updateSupplierScore(String purchaseNo){
        List<PurchaseDetail>  purchaseDetails = purchaseDetailMapper.selectList(new QueryWrapper<PurchaseDetail>().lambda().eq(PurchaseDetail::getPurchaseNo,purchaseNo));
         //[0,25] = -0.5 ,[26,50] -0.25 ..... [50,75] = +0.5 ,[75,100] +0.25
         for (PurchaseDetail purchaseDetail : purchaseDetails) {
             BigDecimal score = new BigDecimal(100);
         //合格率分数
         BigDecimal rateScore =  new BigDecimal(purchaseDetail.getQualifiedQuality())
                   .divide(new BigDecimal(purchaseDetail.getPurchaseQuality()),2, RoundingMode.HALF_UP)
                   .multiply(new BigDecimal(40));
             score = score.add(rateScore);
         DeliverForm  deliverForms =  deliverFormMapper.selectOne(new QueryWrapper<DeliverForm>().lambda()
                    .eq(DeliverForm::getPurchaseNo,purchaseNo));
         //单价分数
         BigDecimal priceRate = purchaseDetail.getPrice().subtract(deliverForms.getPrice())
                 .divide(purchaseDetail.getPrice(),2,RoundingMode.HALF_UP);
         BigDecimal priceScore = BigDecimal.ZERO;
         if (priceRate.compareTo(BigDecimal.ZERO) <= 0){
             priceScore = new BigDecimal(Math.abs(priceRate.floatValue())).multiply(new BigDecimal(30));
         }else {
             priceScore = priceRate.multiply(new BigDecimal(30));
         }
             score = score.add(priceScore);
             //从发送采购订单到到货时间
         if (Period.between(deliverForms.getDeliverDate().toLocalDate(),deliverForms.getTheoryTime().toLocalDate()).getDays()>0){
             score = score.add(new BigDecimal(10));
         };
         //按照采购订单规定数量交货
         BigDecimal timeScore =   new BigDecimal(deliverForms.getNum())
                     .divide(new BigDecimal(deliverForms.getTheoryNum()),2,RoundingMode.HALF_UP)
                     .multiply(new BigDecimal(20));
         score = score.add(timeScore);
        SupplierScore supplierScore =  supplierScoreMapper.selectOne(new QueryWrapper<SupplierScore>().lambda()
                 .eq(SupplierScore::getSupplierId,deliverForms.getSupplierId())
                 .eq(SupplierScore::getMaterialId,purchaseDetail.getProductNo()));
        if (supplierScore != null){
                supplierScore.setSupplierScore(getFastScore(score));
                supplierScoreMapper.update(supplierScore,new QueryWrapper<SupplierScore>().lambda()
                        .eq(SupplierScore::getId,supplierScore.getId()));
        }else {
             supplierScore = new SupplierScore();
             supplierScore.setCreateTime(LocalDateTime.now());
             supplierScore.setSupplierId(deliverForms.getSupplierId());
             supplierScore.setMaterialId(Long.valueOf(purchaseDetail.getProductNo()));
             supplierScore.setSupplierScore(getFastScore(score));
             supplierScoreMapper.insert(supplierScore);
            }
        }
        return RepResult.repResult(0,"成功",null);
    }


    @Async
    public void sendEmail(Map<String,Map<String,String>> map){
        for (String s : map.keySet()) {
            Map<String,String> list = map.get(s);
            Supplier supplier = supplierMapper.selectOne(new QueryWrapper<Supplier>().eq("id", s));
            String model = "尊敬的"+supplier.getSupplier()+"您好,我司向贵公司采购的价格为"+list.get("price")+"元的"+list.get("num")+"份"+list.get("name")+"。因双方意见达成一致，即日签订合同。点击链接即为签订。" ;
            String url = model+"http://localhost:8080/deli/toContract?no="+list.get("no");
            try {
                SendEmail.send(supplier.getEmail(),url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public BigDecimal getFastScore(BigDecimal score){
       BigDecimal  fastScore = BigDecimal.ZERO;
       if (0<=score.floatValue() && score.floatValue()<25){
            fastScore = BigDecimal.valueOf(-1);
       }
       else if (25<=score.floatValue() && score.floatValue()<50){
           fastScore = BigDecimal.valueOf(-0.5);
       }
       else if (50<=score.floatValue() && score.floatValue()<75){
           fastScore = BigDecimal.valueOf(0.5);
       }
       else if (75<=score.floatValue() && score.floatValue()<100){
           fastScore = BigDecimal.valueOf(1);
       }
       return fastScore;
    }

    public static void main(String[] args) {
    }
}
