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
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Date;

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
    private PurchaseFormMapper purchaseFormMapper;
    @Resource
    private ContractMapper contractMapper;
    @Resource
    private SupplierMapper supplierMapper;
    @Resource
    private DeliverFormMapper deliverFormMapper;

    public Object sendContract(String no){
        PurchaseForm purchaseForm = purchaseFormMapper.selectOne(new QueryWrapper<PurchaseForm>().eq("no", no));
        if(StringUtils.isNotBlank(purchaseForm.getContractNo())){
            return RepResult.repResult(0, "该采购单已有合同", null);
        }
        DeliverForm deliverForm = deliverFormMapper.selectOne(new QueryWrapper<DeliverForm>().eq("purchase_no", no));

        PurchaseDetail purchaseDetail = purchaseDetailMapper.selectOne(new QueryWrapper<PurchaseDetail>().eq("purchase_no", no));

        Supplier supplier = supplierMapper.selectOne(new QueryWrapper<Supplier>().eq("id", deliverForm.getSupplierId()));

        Contract contract = new Contract();
        contract.setNo("N"+System.currentTimeMillis());
        contract.setCreateDate(LocalDateTime.now());
        contract.setStatus(0);
        contract.setCreatePerson(WebUtils.getCurrentUserName());
        contract.setUpdateDate(LocalDateTime.now());
        contract.setUpdatePerson(WebUtils.getCurrentUserName());
        contract.setSupplierId(deliverForm.getSupplierId());
        contract.setValidateDateBegin(LocalDateTime.now());
        contract.setValidateDateEnd(DateUtils.getLocalDateTime(DateUtils.getDateAdd(7)));
        if(1!=contractMapper.insert(contract)){
            return RepResult.repResult(0, "生成合同失败", null);
        }
        purchaseForm.setContractNo(contract.getNo());
        if(1!=purchaseFormMapper.update(purchaseForm,new QueryWrapper<PurchaseForm>().eq("no",purchaseForm.getNo()))){
            return RepResult.repResult(0, "保存数据失败", null);
        }
        try {
            SendEmail.send(supplier.getEmail(),"hehe");
        } catch (Exception e) {
            return RepResult.repResult(0, "发送邮件失败", null);
        }
        return RepResult.repResult(0, "生成合同完成并发送邮件成功", null);
    }

}
