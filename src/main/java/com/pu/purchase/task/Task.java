package com.pu.purchase.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pu.purchase.entity.DeliverForm;
import com.pu.purchase.entity.PurchaseDetail;
import com.pu.purchase.entity.SupplierScore;
import com.pu.purchase.mapper.DeliverFormMapper;
import com.pu.purchase.mapper.PurchaseDetailMapper;
import com.pu.purchase.mapper.SupplierScoreMapper;
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
public class Task {

    @Autowired
    private DeliverFormMapper deliverFormMapper;

    @Autowired
    private SupplierScoreMapper supplierScoreMapper;

    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;

    @Scheduled(cron = "0/10 * * * * ?")
    public void updateSendPrice(){

       List<DeliverForm> deliverForm = deliverFormMapper.selectList(new QueryWrapper<DeliverForm>().lambda()
               .in(DeliverForm::getStatus,1,0)
               .le(DeliverForm::getTheoryTime,LocalDateTime.now()));

       Map<String,List<DeliverForm>> listMap = deliverForm.stream().collect(Collectors.groupingBy(DeliverForm::getPurchaseNo));
        for (String s : listMap.keySet()) {
            //所有理论数量相加 如果不满足采购数量 或者到货时间 就剔除掉 选择排名在前的 创建询价
            PurchaseDetail purchaseDetail =  purchaseDetailMapper.selectOne(new QueryWrapper<PurchaseDetail>().lambda()
                    .eq(PurchaseDetail::getPurchaseNo,s));
            Integer sum = 0;
            List<Long> supplierIds = new ArrayList<>();
            int limit = 0;
            for (DeliverForm form : listMap.get(s)) {
                if (form.getTheoryTime().compareTo(purchaseDetail.getArriveTime()) > 0 || form.getTheoryNum() == 0){
                    DeliverForm deliverForm1 = new DeliverForm();
                    deliverForm1.setStatus(-1);
                    deliverFormMapper.update(deliverForm1,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getSupplierId,form.getSupplierId())
                            .eq(DeliverForm::getNo,s));
                    limit = limit + 1;
                    //set为零是为了可以接下来选择其它供应商
                    form.setTheoryNum(0);
                }
                //判断其中某个供应商是否完全符合采购数量
                if (purchaseDetail.getPurchaseQuality().equals(form.getTheoryNum())){
                    DeliverForm deliverForm1 = new DeliverForm();
                    deliverForm1.setStatus(-1);
                    deliverFormMapper.update(deliverForm1,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getPurchaseNo,form.getPurchaseNo())
                            .ne(DeliverForm::getSupplierId,form.getSupplierId()));
                    DeliverForm currentDeliver = new DeliverForm();
                    currentDeliver.setStatus(2);
                    deliverFormMapper.update(currentDeliver,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getPurchaseNo,form.getPurchaseNo())
                            .eq(DeliverForm::getSupplierId,form.getSupplierId()));
                    //发送发货单邮件
                    sum = purchaseDetail.getPurchaseQuality();
                    break;
                }
                //判断多个供应商是否完全符合采购数量
                if (sum.equals(purchaseDetail.getPurchaseQuality())){
                    supplierIds.add(form.getSupplierId());
                    DeliverForm deliverForm1 = new DeliverForm();
                    deliverForm1.setStatus(-1);
                    deliverFormMapper.update(deliverForm1,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getPurchaseNo,form.getPurchaseNo())
                            .notIn(DeliverForm::getSupplierId,supplierIds));
                    DeliverForm currentDeliver = new DeliverForm();
                    currentDeliver.setStatus(2);
                    deliverFormMapper.update(currentDeliver,new QueryWrapper<DeliverForm>().lambda()
                            .eq(DeliverForm::getPurchaseNo,form.getPurchaseNo())
                            .in(DeliverForm::getSupplierId,form.getSupplierId()));
                    //发送发货单邮件
                    sum = purchaseDetail.getPurchaseQuality();
                    break;
                }
                supplierIds.add(form.getSupplierId());
                sum = sum + form.getTheoryNum();
            }
            //选择新的供应商
            if (limit != 0 && !sum.equals(purchaseDetail.getPurchaseQuality())){
               List<SupplierScore> supplierScores = supplierScoreMapper.selectList(new QueryWrapper<SupplierScore>().lambda()
                        .eq(SupplierScore::getMaterialId,purchaseDetail.getProductNo())
                        .notIn(SupplierScore::getSupplierId,supplierIds)
                        .orderByDesc(SupplierScore::getSupplierScore)
                        .last("limit "+limit));
                for (SupplierScore supplierScore : supplierScores) {
                    DeliverForm deliver = new DeliverForm();
                    deliver.setSupplierId(supplierScore.getId());
                    deliver.setPurchaseNo(purchaseDetail.getPurchaseNo());
                    deliver.setNo("SN"+System.currentTimeMillis());
                    deliverFormMapper.insert(deliver);
                    //发送询价单邮件
                }
            }

        }
    }









}
