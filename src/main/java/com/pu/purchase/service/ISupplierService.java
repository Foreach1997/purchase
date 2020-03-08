package com.pu.purchase.service;

import com.pu.purchase.entity.DeliverForm;
import com.pu.purchase.entity.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 供应商/客户信息表 服务类
 * </p>
 *
 * @author 
 * @since 2020-03-01
 */
public interface ISupplierService extends IService<Supplier> {

    /**
     * 获取所有供应商
     */
    Object getAllSupplier(int current,int size,String supplier,String phonenum,Integer enabled);

    /**
     * 获取询价供应商
     */
    Object getAllSupplierScore(int current,int size,Long materialId);

    /**
     * 更新供应商评分
     */
    Object updateSupplierScore(String purchaseNo);
    /**
     * 询价供应商
     */
    Object inquiryPrice(List<DeliverForm> deliverForms);


    Object insertInquiryPrice(DeliverForm deliverForm);
}
