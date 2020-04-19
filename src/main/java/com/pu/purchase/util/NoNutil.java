package com.pu.purchase.util;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pu.purchase.entity.PurchaseForm;
import com.pu.purchase.mapper.PurchaseFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class NoNutil {

    @Resource
    private PurchaseFormMapper purchaseFormMapper;

    public String getAllNo(){
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+ " 23:59:59";
        String start = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+ " 00:00:00";
        String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count =  purchaseFormMapper.selectCount(new QueryWrapper<PurchaseForm>().lambda()
                .gt(PurchaseForm::getCreateDate,start)
                .lt(PurchaseForm::getCreateDate,end));
        count = count + 1;
        String no = nowDate + count;
        return "CG"+no;
    }









}
