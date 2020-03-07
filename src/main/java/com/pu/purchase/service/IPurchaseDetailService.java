package com.pu.purchase.service;

import com.pu.purchase.entity.PurchaseDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pu.purchase.vo.ReqStaticfic;
import com.pu.purchase.vo.StaticficPurchaseVO;

import java.util.List;

/**
 * <p>
 * 采购明细表 服务类
 * </p>
 *
 * @author 
 * @since 2020-03-01
 */
public interface IPurchaseDetailService extends IService<PurchaseDetail> {


    List<StaticficPurchaseVO> staticficPurchase(ReqStaticfic reqStaticfic);


}
