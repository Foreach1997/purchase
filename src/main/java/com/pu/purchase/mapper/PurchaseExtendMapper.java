package com.pu.purchase.mapper;

import com.pu.purchase.vo.ReqStaticfic;
import com.pu.purchase.vo.StaticficPurchaseVO;

import java.util.List;

public interface PurchaseExtendMapper {


    List<StaticficPurchaseVO> staticficPurchase(ReqStaticfic reqStaticfic);




}
