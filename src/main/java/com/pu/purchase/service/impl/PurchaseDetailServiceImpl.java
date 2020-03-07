package com.pu.purchase.service.impl;

import com.pu.purchase.entity.PurchaseDetail;
import com.pu.purchase.mapper.PurchaseDetailMapper;
import com.pu.purchase.mapper.PurchaseExtendMapper;
import com.pu.purchase.service.IPurchaseDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.vo.ReqStaticfic;
import com.pu.purchase.vo.StaticficPurchaseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 采购明细表 服务实现类
 * </p>
 *
 * @author 
 * @since 2020-03-01
 */
@Service
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailMapper, PurchaseDetail> implements IPurchaseDetailService {

    @Autowired
    private PurchaseExtendMapper purchaseExtendMapper;

    @Override
    public List<StaticficPurchaseVO> staticficPurchase(ReqStaticfic reqStaticfic) {
        return purchaseExtendMapper.staticficPurchase(reqStaticfic);
    }
}
