package com.pu.purchase.service;

import com.pu.purchase.entity.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;

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


}
