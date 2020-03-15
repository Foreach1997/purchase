package com.pu.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.pu.purchase.entity.*;
import com.pu.purchase.mapper.*;
import com.pu.purchase.service.IContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.util.DateUtils;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.util.SendEmail;
import com.pu.purchase.util.WebUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.rmi.MarshalledObject;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 合同表 服务实现类
 * </p>
 *
 * @author 
 * @since 2020-03-01
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements IContractService {

    @Resource
    private PurchaseDetailMapper purchaseDetailMapper;
    @Resource
    private ContractMapper contractMapper;
    @Resource
    private MaterialMapper materialMapper;
    @Resource
    private DeliverFormMapper deliverFormMapper;
    @Resource
    private SupplierServiceImpl supplierService;
    @Resource
    private PurchaseFormMapper purchaseFormMapper;

    public Object sendContract(String no){
        List<Contract> contracts = contractMapper.selectList(new QueryWrapper<Contract>().eq("purchase_no", no));
        if(null!=contracts){
            return RepResult.repResult(0, "您已发送过请勿重复发送", null);
        }

        List<DeliverForm> deliverFormList = deliverFormMapper.selectList(new QueryWrapper<DeliverForm>().eq("purchase_no", no));
        Map<String,Map<String,String>> mapMap = new HashMap<>();
        for (DeliverForm deliverForm : deliverFormList) {
            Map<String,String> map = new HashMap<>();
            Contract contract = new Contract();
            String number = "N"+System.currentTimeMillis();
            contract.setNo(number);
            contract.setCreateDate(LocalDateTime.now());
            contract.setStatus(0);
            contract.setCreatePerson(WebUtils.getCurrentUserName());
            contract.setUpdateDate(LocalDateTime.now());
            contract.setUpdatePerson(WebUtils.getCurrentUserName());
            contract.setSupplierId(deliverForm.getSupplierId());
            contract.setPurchaseNo(deliverForm.getPurchaseNo());
            contract.setValidateDateBegin(LocalDateTime.now());
            contract.setValidateDateEnd(DateUtils.getLocalDateTime(DateUtils.getDateAdd(7)));
            if(1!=contractMapper.insert(contract)){
                return RepResult.repResult(0, "生成合同失败", null);
            }
            PurchaseDetail purchaseDetail = purchaseDetailMapper.selectOne(new QueryWrapper<PurchaseDetail>().eq("purchase_no", no));
            Material material = materialMapper.selectOne(new QueryWrapper<Material>().eq("id", purchaseDetail.getProductNo()));
            map.put("no",number);
            map.put("price",deliverForm.getPrice().toString());
            map.put("num",deliverForm.getNum().toString());
            map.put("name",material.getName());
            mapMap.put(deliverForm.getSupplierId().toString(),map);
        }
        supplierService.sendEmail(mapMap);
        return RepResult.repResult(0, "生成合同完成并发送邮件成功", null);
    }

}
