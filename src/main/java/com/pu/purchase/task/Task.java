package com.pu.purchase.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pu.purchase.entity.DeliverForm;
import com.pu.purchase.entity.PurchaseDetail;
import com.pu.purchase.mapper.DeliverFormMapper;
import com.pu.purchase.mapper.PurchaseDetailMapper;
import com.pu.purchase.mapper.SupplierScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Scheduled()
    public void updateSendPrice(){

       List<DeliverForm> deliverForm = deliverFormMapper.selectList(new QueryWrapper<DeliverForm>().lambda());

       Map<String,List<DeliverForm>> listMap = deliverForm.stream().collect(Collectors.groupingBy(DeliverForm::getPurchaseNo));
        for (String s : listMap.keySet()) {
            //所有理论数量相加 如果不满足采购数量 或者到货时间 就剔除掉 选择排名在前的 创建询价
//            List<DeliverForm> deliverForms = deliverFormMapper.selectList(new QueryWrapper<DeliverForm>().lambda().eq(DeliverForm::getPurchaseNo,s)
//                    .last("theory_num is null"));
            PurchaseDetail purchaseDetail =  purchaseDetailMapper.selectOne(new QueryWrapper<PurchaseDetail>().lambda()
                    .eq(PurchaseDetail::getPurchaseNo,s));
            Integer sum = 0;
            for (DeliverForm form : listMap.get(s)) {
              //  if (form.getTheoryTime().compareTo())
                sum = sum + form.getTheoryNum();
            }
            List<DeliverForm> deliverForms = deliverFormMapper.selectList(new QueryWrapper<DeliverForm>().lambda()
                    .eq(DeliverForm::getPurchaseNo,s));
            List<Long> delDeliverForm = deliverForms.stream().map(DeliverForm::getSupplierId).collect(Collectors.toList());
            DeliverForm deliverForm1 = new DeliverForm();
            deliverForm1.setStatus(-1);
            deliverFormMapper.update(deliverForm1,new QueryWrapper<DeliverForm>().lambda()
                        .in(DeliverForm::getSupplierId,delDeliverForm)
                        .eq(DeliverForm::getNo,s));

        }
    }









}
