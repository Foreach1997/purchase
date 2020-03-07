package com.pu.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.pu.purchase.entity.Supplier;
import com.pu.purchase.mapper.SupplierMapper;
import com.pu.purchase.service.ISupplierService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.util.RepResult;
import org.hibernate.validator.constraints.pl.REGON;
import com.pu.purchase.util.RepResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


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

    @Autowired
    private SupplierMapper supplierMapper;


   public  Object getAllSupplier(int current,int size,String supplier,String phonenum,Integer enabled){
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
        return RepResult.repResult(0, "查询成功", supplierList, supplierList.size());
    }

}
