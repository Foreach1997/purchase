package com.pu.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.config.BizException;
import com.pu.purchase.dto.DeliverFormDto;
import com.pu.purchase.dto.GoodsDto;
import com.pu.purchase.entity.*;
import com.pu.purchase.mapper.DeliverFormMapper;
import com.pu.purchase.mapper.GoodsClassifyMapper;
import com.pu.purchase.mapper.MaterialMapper;
import com.pu.purchase.mapper.PurchaseDetailMapper;
import com.pu.purchase.service.IMaterialService;
import com.pu.purchase.util.DateUtils;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.vo.DeliverFormVo;
import com.pu.purchase.vo.MaterialVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 产品表 服务实现类
 * </p>
 *
 * @author 
 * @since 2020-03-01
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements IMaterialService {

    @Resource
    private MaterialMapper materialMapper;
    @Resource
    private DeliverFormMapper deliverFormMapper;
    @Resource
    private SupplierServiceImpl supplierService;
    @Resource
    private PurchaseDetailMapper purchaseDetailMapper;
    @Resource
    private GoodsClassifyMapper goodsClassifyMapper;

    public Object getMaterialList(){
        List<Material> materials = materialMapper.selectList(new QueryWrapper<Material>().eq("delete_Flag", "0"));
        List<Material> materials1 = new ArrayList<>();
        for (Material material : materials) {
            GoodsClassify goodsClassify = goodsClassifyMapper.selectById(material.getClassifyId());
            material.setName(material.getName()+"("+goodsClassify.getClassifyName()+")");
            materials1.add(material);
        }
        return RepResult.repResult(0, "查询成功", materials1, (long) materials.size());
    }


    public Object loadAllPurchaseForm(MaterialVo materialVo){
        LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<Material>()
                .eq(StringUtils.isNotBlank(materialVo.getName()),Material::getName,materialVo.getName())
                .eq(Material::getDeleteFlag,"0");
        Page<Material> materialPage = materialMapper.selectPage(new Page<>(materialVo.getPage(), materialVo.getLimit()), queryWrapper);
        List<GoodsDto> goodsDtos = materialPage.getRecords().stream().map(material -> {
            GoodsClassify goodsClassify = goodsClassifyMapper.selectOne(new LambdaQueryWrapper<GoodsClassify>().eq(GoodsClassify::getId, material.getClassifyId()));
            GoodsDto goodsDto = new GoodsDto();
            goodsDto.setId(material.getId().toString());
            goodsDto.setName(material.getName());
            goodsDto.setGoodsIfyName(goodsClassify.getClassifyName());
            return goodsDto;
        }).collect(Collectors.toList());
        return RepResult.repResult(0, "成功",goodsDtos,materialPage.getTotal());
    }

    public Object getSupplierList(){
        List<GoodsClassify> supplierList = goodsClassifyMapper.selectList(new QueryWrapper<>());
        return RepResult.repResult(0, "查询成功", supplierList, (long) supplierList.size());
    }

    public Object insertSelective(MaterialVo record) {
        if (null == record) {
            throw new BizException("添加数据为空");
        }
        record.setDeleteFlag("0");
        if (1 != materialMapper.insert(record)) {
            throw new BizException("添加失败");
        }
        return RepResult.repResult(0, "添加成功", null);
    }

    public Object insertGoodsClass(GoodsClassify record) {
        if (null == record) {
            throw new BizException("添加数据为空");
        }
        if (1 != goodsClassifyMapper.insert(record)) {
            throw new BizException("添加失败");
        }
        return RepResult.repResult(0, "添加成功", null);
    }

    public Object updateByPrimaryKeySelective(MaterialVo record) {
        if (null == record) {
            throw new BizException("修改数据为空");
        }
        if (1 != materialMapper.update(record, new QueryWrapper<Material>().eq("id", record.getId()))) {
            throw new BizException("修改失败");
        }
        return RepResult.repResult(0, "修改成功", null);
    }


    public Object deleteByPrimaryKey(String id) {
        if (null == id) {
            throw new BizException("删除id为空");
        }
        Material record = materialMapper.selectOne(new QueryWrapper<Material>().eq("id", id));
        Material material = record.setDeleteFlag("1");
        if (1 != materialMapper.update(material, new QueryWrapper<Material>().eq("id", record.getId()))) {
            throw new BizException("删除失败");
        }
        return RepResult.repResult(0, "删除成功", null);
    }

    public Object loadAllDeliver(DeliverFormVo deliverFormVo){
        LambdaQueryWrapper<DeliverForm> queryWrapper = new LambdaQueryWrapper<DeliverForm>()
                .eq(StringUtils.isNotBlank(deliverFormVo.getPurchaseNo()),DeliverForm::getPurchaseNo,deliverFormVo.getPurchaseNo())
                .eq(StringUtils.isNotBlank(deliverFormVo.getSupplierId()),DeliverForm::getSupplierId,deliverFormVo.getSupplierId());
        Page<DeliverForm> materialPage = deliverFormMapper.selectPage(new Page<>(deliverFormVo.getPage(), deliverFormVo.getLimit()), queryWrapper);
        List<DeliverFormDto> collect = new ArrayList<>();
        for (DeliverForm material : materialPage.getRecords()) {
            DeliverFormDto deliverFormDto = new DeliverFormDto();
            BeanUtils.copyProperties(material,deliverFormDto);
            deliverFormDto.setCreateDate(DateUtils.dateFrString(material.getCreateDate()));
            if(null!=material.getDeliverDate()){
                deliverFormDto.setDeliverDate(DateUtils.dateFrString(material.getDeliverDate()));
            }
            if(null!=material.getTheoryTime()){
                deliverFormDto.setTheoryTime(DateUtils.dateFrString(material.getTheoryTime()));
            }
            if(null!=material.getUpdateDate()) {
                deliverFormDto.setUpdateDate(DateUtils.dateFrString(material.getUpdateDate()));
            }
            collect.add(deliverFormDto);
        }
        return RepResult.repResult(0, "成功", collect,materialPage.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object updateDeliver(DeliverFormVo deliverFormVo){

        DeliverForm deliverForm = deliverFormMapper.selectOne(new QueryWrapper<DeliverForm>().eq("no", deliverFormVo.getNo()));
        deliverForm.setStatus(Integer.parseInt(deliverFormVo.getStatus()));

        if("4".equals(deliverFormVo.getStatus())){
            PurchaseDetail purchaseDetail = purchaseDetailMapper.selectOne(new QueryWrapper<PurchaseDetail>().eq("purchase_no", deliverForm.getPurchaseNo()));
            if(purchaseDetail.getPurchaseQuality()<deliverFormVo.getQualifiedQuality() || purchaseDetail.getPurchaseQuality()<deliverFormVo.getStorageQuality()){
                return RepResult.repResult(0, "请填写正确数量", null);
            }
            Integer storageQuality = purchaseDetail.getStorageQuality();
            if(null==storageQuality){
                storageQuality = 0;
            }
            purchaseDetail.setStorageQuality(storageQuality+deliverFormVo.getStorageQuality());
            deliverForm.setQualifiedQuality(deliverFormVo.getQualifiedQuality());
            purchaseDetailMapper.update(purchaseDetail,new QueryWrapper<PurchaseDetail>().eq("purchase_no", deliverForm.getPurchaseNo()));
        }
        if(1!= deliverFormMapper.update(deliverForm,new QueryWrapper<DeliverForm>().eq("no",deliverFormVo.getNo()))){
            return RepResult.repResult(0, "修改失败", null);
        }
        supplierService.updateSupplierScore(deliverForm.getPurchaseNo(),deliverForm.getSupplierId());
        return RepResult.repResult(0, "成功", null);
    }




}
