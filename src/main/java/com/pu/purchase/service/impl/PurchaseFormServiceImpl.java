package com.pu.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pu.purchase.config.BizException;
import com.pu.purchase.dto.PurchaseFormDto;
import com.pu.purchase.dto.UserDto;
import com.pu.purchase.entity.PurchaseDetail;
import com.pu.purchase.entity.PurchaseForm;
import com.pu.purchase.entity.User;
import com.pu.purchase.mapper.PurchaseDetailMapper;
import com.pu.purchase.mapper.PurchaseFormMapper;
import com.pu.purchase.service.IPurchaseFormService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.util.DateUtils;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.util.WebUtils;
import com.pu.purchase.vo.PurchaseFormVo;
import com.pu.purchase.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 采购单表 服务实现类
 * </p>
 *
 * @author 
 * @since 2020-03-01
 */
@Service
public class PurchaseFormServiceImpl extends ServiceImpl<PurchaseFormMapper, PurchaseForm> implements IPurchaseFormService {


    @Resource
    private PurchaseFormMapper purchaseFormMapper;
    @Resource
    private PurchaseDetailMapper purchaseDetailMapper;


    public Object queryAllPurchaseForm(PurchaseFormVo purchaseFormVo) {
        LambdaQueryWrapper<PurchaseForm> queryWrapper = new LambdaQueryWrapper<PurchaseForm>()
                .eq(StringUtils.isNotBlank(purchaseFormVo.getNo()), PurchaseForm::getNo, purchaseFormVo.getNo())
                .eq(null!=purchaseFormVo.getSupplierId(),PurchaseForm::getSupplierId,purchaseFormVo.getSupplierId())
                .eq(StringUtils.isNotBlank(purchaseFormVo.getName()),PurchaseForm::getName,purchaseFormVo.getName())
                .eq(null!=purchaseFormVo.getStatus(),PurchaseForm::getStatus,purchaseFormVo.getStatus());
        IPage<PurchaseForm> purchaseFormPage = purchaseFormMapper.selectPage(new Page<>(purchaseFormVo.getPage(),purchaseFormVo.getLimit()), queryWrapper);
        List<PurchaseFormDto> dtoList = purchaseFormPage.getRecords().stream().map(purchaseForm -> {
            PurchaseFormDto purchaseFormDto = new PurchaseFormDto();
            BeanUtils.copyProperties(purchaseForm,purchaseFormDto);
            PurchaseDetail purchaseDetail = purchaseDetailMapper.selectOne(new QueryWrapper<PurchaseDetail>().eq("purchase_no", purchaseForm.getNo()));
            purchaseFormDto.setProductNo(purchaseDetail.getProductNo());
            purchaseFormDto.setPurchaseQuality(purchaseDetail.getPurchaseQuality());
            purchaseFormDto.setQualifiedQuality(purchaseDetail.getQualifiedQuality());
            purchaseFormDto.setStorageQuality(purchaseDetail.getStorageQuality());
            purchaseFormDto.setPurchasePrice(purchaseDetail.getPurchasePrice());
            switch (purchaseForm.getStatus()){
                case -1:
                    purchaseFormDto.setStatus("已作废");
                    break;
                case 0:
                    purchaseFormDto.setStatus("待确认");
                    break;
                case 1:
                    purchaseFormDto.setStatus("已确认");
                    break;
                default:
                    purchaseFormDto.setStatus("已完成");
                    break;
            }
            return purchaseFormDto;
        }).collect(Collectors.toList());
        return RepResult.repResult(0, "查询成功", dtoList, purchaseFormPage.getTotal());
    }

    public Object insertSelective(PurchaseFormVo record) {
        if (null == record) {
            throw new BizException("添加数据为空");
        }
        Long time = new Date().getTime();
        record.setNo(time.toString());
        record.setCreatePerson(WebUtils.getCurrentUserName());
        record.setUpdateDate(LocalDateTime.now());
        record.setCreateDate(LocalDateTime.now());
        record.setStatus(0);
        if (1 != purchaseFormMapper.insert(record)) {
            throw new BizException("添加采购单失败");
        }
        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setProductNo(record.getProductNo());
        purchaseDetail.setPurchasePrice(record.getPurchasePrice());
        purchaseDetail.setPurchaseQuality(record.getPurchaseQuality());
        purchaseDetail.setPurchaseNo(time.toString());
        if(1 != purchaseDetailMapper.insert(purchaseDetail)){
            throw new BizException("添加采购单详情失败");
        }
        return RepResult.repResult(0, "添加成功", null);
    }

    public Object updateByPrimaryKeySelective(PurchaseFormVo record) {
        if (null == record) {
            throw new BizException("修改数据为空");
        }
        if (1 != purchaseFormMapper.update(record,new QueryWrapper<PurchaseForm>().eq("no",record.getNo()))) {
            throw new BizException("修改采购单失败");
        }
        return RepResult.repResult(0, "修改成功", null);
    }



    public Object deleteByPrimaryKey(String id) {
        if (null == id) {
            throw new BizException("删除id为空");
        }
        if (1 != purchaseFormMapper.deleteById(id)) {
            throw new BizException("删除采购单失败");
        }
        return RepResult.repResult(0, "删除成功", null);
    }

}
