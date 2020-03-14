package com.pu.purchase.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pu.purchase.entity.DeliverForm;
import com.pu.purchase.entity.PurchaseDetail;
import com.pu.purchase.entity.Supplier;
import com.pu.purchase.entity.SupplierScore;
import com.pu.purchase.mapper.DeliverFormMapper;
import com.pu.purchase.mapper.PurchaseDetailMapper;
import com.pu.purchase.mapper.SupplierMapper;
import com.pu.purchase.mapper.SupplierScoreMapper;
import com.pu.purchase.util.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Task {

    @Autowired
    private DeliverFormMapper deliverFormMapper;

    @Autowired
    private SupplierScoreMapper supplierScoreMapper;

    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;

    @Autowired
    private SupplierMapper supplierMapper;

    //@Scheduled(cron = "0/10 * * * * ?")
    public void updateSendPrice(){
       List<DeliverForm> deliverForm = deliverFormMapper.selectList(new QueryWrapper<DeliverForm>().lambda()
               .in(DeliverForm::getStatus,1,0)
               .ge(DeliverForm::getTheoryTime,LocalDateTime.now()));

       Map<String,List<DeliverForm>> listMap = deliverForm.stream().collect(Collectors.groupingBy(DeliverForm::getPurchaseNo));
        for (String s : listMap.keySet()) {
            //所有理论数量相加 如果不满足采购数量 或者到货时间 就剔除掉 选择排名在前的 创建询价
            PurchaseDetail purchaseDetail =  purchaseDetailMapper.selectOne(new QueryWrapper<PurchaseDetail>().lambda()
                    .eq(PurchaseDetail::getPurchaseNo,s));
            Integer sum = 0;
            List<DeliverForm> deliverForms = new ArrayList<>();
            int limit = 0;
            for (DeliverForm form : listMap.get(s)) {
                if (form.getTheoryTime().compareTo(purchaseDetail.getArriveTime()) < 0 || form.getTheoryNum() == 0){
                    DeliverForm deliverForm1 = new DeliverForm();
                    deliverForm1.setStatus(-1);
                    deliverFormMapper.update(deliverForm1,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getSupplierId,form.getSupplierId())
                            .eq(DeliverForm::getNo,form.getNo()));
                    limit = limit + 1;
                    //set为零是为了可以接下来选择其它供应商
                    form.setTheoryNum(0);
                }
                //判断其中某个供应商是否完全符合采购数量
                if (purchaseDetail.getPurchaseQuality().equals(form.getTheoryNum())){
                    DeliverForm deliverForm1 = new DeliverForm();
                    deliverForm1.setStatus(-1);
                    deliverFormMapper.update(deliverForm1,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getNo,form.getNo())
                            .ne(DeliverForm::getSupplierId,form.getSupplierId()));
                    DeliverForm currentDeliver = new DeliverForm();
                    currentDeliver.setStatus(2);
                    deliverFormMapper.update(currentDeliver,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getPurchaseNo,form.getPurchaseNo())
                            .eq(DeliverForm::getSupplierId,form.getSupplierId()));
                    //发送发货单邮件
                    Supplier supplier =  supplierMapper.selectOne(new QueryWrapper<Supplier>().lambda().eq(Supplier::getId,form.getSupplierId()));
                    try {
                        SendEmail.send(supplier.getEmail(), "http://localhost:8080/deli/toDeliverForm?no=" + form.getNo());
                    }catch (Exception e){
                        log.info(e.getMessage());
                    }
                    sum = purchaseDetail.getPurchaseQuality();
                    break;
                }

                sum = sum + form.getTheoryNum();
                //deliverForms.add(form);
                //判断多个供应商是否完全符合采购数量
                if (sum.equals(purchaseDetail.getPurchaseQuality())){
                    deliverForms.add(form);
                    DeliverForm deliverForm1 = new DeliverForm();
                    deliverForm1.setStatus(-1);
                    deliverFormMapper.update(deliverForm1,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getPurchaseNo,form.getPurchaseNo())
                            .notIn(DeliverForm::getSupplierId,deliverForms.stream().map(DeliverForm::getSupplierId).collect(Collectors.toList())));
                    DeliverForm currentDeliver = new DeliverForm();
                    currentDeliver.setStatus(2);
                    deliverFormMapper.update(currentDeliver,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getPurchaseNo,form.getPurchaseNo())
                            .in(DeliverForm::getSupplierId,deliverForms.stream().map(DeliverForm::getSupplierId).collect(Collectors.toList())));
                    //发送发货单邮件
                    sum = purchaseDetail.getPurchaseQuality();
                    for (DeliverForm deliverForm2 : deliverForms) {
                        Supplier supplier =  supplierMapper.selectOne(new QueryWrapper<Supplier>().lambda()
                                .eq(Supplier::getId,deliverForm2.getSupplierId()));
                        try {
                            SendEmail.send(supplier.getEmail(), "http://localhost:8080/deli/toDeliverForm?no" + deliverForm2.getNo());
                        }catch (Exception e){
                            log.info(e.getMessage());
                        }
                    }
                    break;
                }
                deliverForms.add(form);
            }
            //选择新的供应商
            if (!sum.equals(purchaseDetail.getPurchaseQuality())){
               if (limit == 0){
                    limit = 1;
               }
               List<SupplierScore> supplierScores = supplierScoreMapper.selectList(new QueryWrapper<SupplierScore>().lambda()
                        .eq(SupplierScore::getMaterialId,purchaseDetail.getProductNo())
                        .notIn(SupplierScore::getSupplierId,deliverForms.stream().map(DeliverForm::getSupplierId).collect(Collectors.toList()))
                        .orderByDesc(SupplierScore::getSupplierScore)
                        .last("limit "+limit));
                for (SupplierScore supplierScore : supplierScores) {
                    DeliverForm deliver = new DeliverForm();
                    deliver.setSupplierId(supplierScore.getSupplierId());
                    deliver.setPurchaseNo(purchaseDetail.getPurchaseNo());
                    deliver.setNo("SN"+System.currentTimeMillis());
                    deliverFormMapper.insert(deliver);
                    //发送询价单邮件
                    Supplier supplier =  supplierMapper.selectOne(new QueryWrapper<Supplier>().lambda()
                            .eq(Supplier::getId,deliver.getSupplierId()));
                    try {
                        SendEmail.send(supplier.getEmail(), "http://localhost:8080/purMsg/inquiryOrder.html?no=" + deliver.getNo());
                    }catch (Exception e){
                        log.info(e.getMessage());
                    }
                }
            }

        }
    }

    //@Scheduled(cron = "0/10 * * * * ?")
//    public void sendTo(){
//        deliverFormMapper.update();
//    }






}
