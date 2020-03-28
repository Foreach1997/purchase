package com.pu.purchase.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pu.purchase.entity.*;
import com.pu.purchase.mapper.*;
import com.pu.purchase.util.DateUtils;
import com.pu.purchase.vo.ContractVo;
import com.pu.purchase.vo.DeliverFormVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/deli")
public class DeliverController {

    @Resource
    private DeliverFormMapper deliverFormMapper;
    @Resource
    private PurchaseDetailMapper purchaseDetailMapper;
    @Resource
    private PurchaseFormMapper purchaseFormMapper;
    @Resource
    private MaterialMapper materialMapper;
    @Resource
    private SupplierMapper supplierMapper;
    @Resource
    private ContractMapper contractMapper;



    /**
     * 跳转至发货单页面
     */
    @RequestMapping("/toDeliverForm")
    public ModelAndView toDeliverForm(String no) {
        ModelAndView modelAndView = new ModelAndView();
        DeliverFormVo deliverFormVo = new DeliverFormVo();
        DeliverForm deliverForm = deliverFormMapper.selectOne(new QueryWrapper<DeliverForm>().eq("no", no));
        PurchaseDetail purchaseDetail = purchaseDetailMapper.selectOne(new QueryWrapper<PurchaseDetail>().eq("purchase_no", deliverForm.getPurchaseNo()));
        Material material = materialMapper.selectOne(new QueryWrapper<Material>().eq("id", purchaseDetail.getProductNo()));

        deliverFormVo.setPurchaseNo(deliverForm.getPurchaseNo());
        deliverFormVo.setSupplierId(deliverForm.getSupplierId().toString());
        deliverFormVo.setNo(deliverForm.getNo());
        deliverFormVo.setMaterialName(material.getName());
        deliverFormVo.setTheoryNum(deliverForm.getTheoryNum().toString());
        deliverFormVo.setTheoryPrice(purchaseDetail.getPrice().toString());
        modelAndView.setViewName("DeilMag/DeilverManager");
        modelAndView.addObject("deli",deliverFormVo);
        return modelAndView;
    }

    /**
     * 更新发货单
     */
    @PostMapping("/submit")
    public ModelAndView submit(@ModelAttribute DeliverFormVo deliverFormVo) {
        ModelAndView modelAndView = new ModelAndView();
        DeliverForm deliverForm = new DeliverForm();
        Supplier supplier = supplierMapper.selectOne(new QueryWrapper<Supplier>().eq("id", deliverFormVo.getSupplierId()));

        DeliverForm deliverForm1 = deliverFormMapper.selectOne(new QueryWrapper<DeliverForm>().eq("no", deliverFormVo.getNo()));
        //deliverForm.setStatus(4);
        deliverForm.setDeliverDate(DateUtils.getLocalDateTime(deliverFormVo.getTheoryTime()));
        deliverForm.setUpdatePerson(supplier.getSupplier());
        deliverForm.setUpdateDate(LocalDateTime.now());
        deliverForm.setPurchaseNo(deliverFormVo.getPurchaseNo());
        deliverForm.setSupplierId(Long.parseLong(deliverFormVo.getSupplierId()));
        deliverForm.setNo(deliverFormVo.getNo());
        deliverForm.setPrice(new BigDecimal(deliverFormVo.getPrice()));
        deliverForm.setNum(Integer.parseInt(deliverFormVo.getNum()));
        if(null==deliverForm1.getNum()){
            modelAndView.addObject("msg","您已提交过请勿重复提交");
        }else if(Integer.parseInt(deliverFormVo.getNum())>Integer.parseInt(deliverFormVo.getTheoryNum())){
            modelAndView.addObject("msg","数量过多请重新填写");
        }else {
            modelAndView.addObject("msg","您的发货单已提交请尽快发货");
            deliverFormMapper.update(deliverForm,new QueryWrapper<DeliverForm>().eq("no",deliverFormVo.getNo()));
        }

        modelAndView.setViewName("DeilMag/hello");
        return modelAndView;
    }

    /**
     * 跳转至合同页面
     */
    @RequestMapping("/toContract")
    public ModelAndView toContract(String no) {
        ModelAndView modelAndView = new ModelAndView();
        Contract contract = contractMapper.selectOne(new QueryWrapper<Contract>().eq("no", no));
        List<DeliverForm> deliverForms = deliverFormMapper.selectList(new QueryWrapper<DeliverForm>().eq("purchase_no", contract.getPurchaseNo()));
        boolean bool = true;
        for (DeliverForm deliverForm : deliverForms) {
            if(3!=deliverForm.getStatus() && 2!=deliverForm.getStatus()){
                bool = false;
            }
        }
        if(bool){
            PurchaseForm purchaseForm = purchaseFormMapper.selectOne(new QueryWrapper<PurchaseForm>().eq("no", contract.getPurchaseNo()));
            purchaseForm.setStatus(2);
            purchaseFormMapper.update(purchaseForm,new QueryWrapper<PurchaseForm>().eq("no",contract.getPurchaseNo()));
        }
        contract.setStatus(3);
        contractMapper.update(contract, new QueryWrapper<Contract>().eq("no", no));
        DeliverForm deliverForm = deliverFormMapper.selectOne(new LambdaQueryWrapper<DeliverForm>()
                .eq(DeliverForm::getPurchaseNo, contract.getPurchaseNo())
                .eq(DeliverForm::getSupplierId,contract.getSupplierId()));
        String num = deliverForm.getNum().toString();
        ContractVo contractVo = new ContractVo();
        contractVo.setCreatePerson(contract.getCreatePerson());
        contractVo.setNo(contract.getNo());
        contractVo.setNum(num);
        contractVo.setUnitPrice(deliverForm.getPrice().toString());
        contractVo.setPrice(deliverForm.getPrice().multiply(BigDecimal.valueOf(Long.parseLong(num))).toString());
        contractVo.setPurchaseNo(contract.getPurchaseNo());
        contractVo.setValidateDateBegin(DateUtils.dateFrString(contract.getValidateDateBegin()));
        contractVo.setValidateDateEnd(DateUtils.dateFrString(contract.getValidateDateEnd()));
        modelAndView.setViewName("ConMsg/ContractManager");
        modelAndView.addObject("deli",contractVo);
        return modelAndView;
    }




}
