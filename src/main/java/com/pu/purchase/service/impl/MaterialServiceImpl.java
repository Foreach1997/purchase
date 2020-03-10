package com.pu.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.config.BizException;
import com.pu.purchase.dto.DeliverFormDto;
import com.pu.purchase.entity.DeliverForm;
import com.pu.purchase.entity.Material;
import com.pu.purchase.mapper.DeliverFormMapper;
import com.pu.purchase.mapper.MaterialMapper;
import com.pu.purchase.service.IMaterialService;
import com.pu.purchase.util.DateUtils;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.vo.DeliverFormVo;
import com.pu.purchase.vo.MaterialVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    public Object getMaterialList(){
        List<Material> materials = materialMapper.selectList(new QueryWrapper<Material>().eq("delete_Flag", "0"));
        return RepResult.repResult(0, "查询成功", materials, (long) materials.size());
    }

    public Object loadAllPurchaseForm(MaterialVo materialVo){
        LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<Material>()
                .eq(StringUtils.isNotBlank(materialVo.getName()),Material::getName,materialVo.getName())
                .eq(Material::getDeleteFlag,"0");
        Page<Material> materialPage = materialMapper.selectPage(new Page<>(materialVo.getPage(), materialVo.getLimit()), queryWrapper);
        return RepResult.repResult(0, "成功", materialPage.getRecords(),materialPage.getTotal());
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
        List<DeliverFormDto> collect = materialPage.getRecords().stream().map(material ->{
            DeliverFormDto deliverFormDto = new DeliverFormDto();
            BeanUtils.copyProperties(material,deliverFormDto);
            deliverFormDto.setCreateDate(DateUtils.dateFrString(material.getCreateDate()));
            deliverFormDto.setDeliverDate(DateUtils.dateFrString(material.getDeliverDate()));
            deliverFormDto.setTheoryTime(DateUtils.dateFrString(material.getTheoryTime()));
            deliverFormDto.setUpdateDate(DateUtils.dateFrString(material.getUpdateDate()));
            return deliverFormDto;
        }).collect(Collectors.toList());
        return RepResult.repResult(0, "成功", collect,materialPage.getTotal());
    }

    public Object updateDeliver(DeliverFormVo deliverFormVo){

        DeliverForm deliverForm = deliverFormMapper.selectOne(new QueryWrapper<DeliverForm>().eq("no", deliverFormVo.getNo()));
        supplierService.updateSupplierScore(deliverForm.getPurchaseNo(),deliverForm.getSupplierId());
        deliverForm.setStatus(Integer.parseInt(deliverFormVo.getStatus()));
        if(1!= deliverFormMapper.update(deliverForm,new QueryWrapper<DeliverForm>().eq("no",deliverFormVo.getNo()))){
            return RepResult.repResult(0, "修改失败", null);
        }
        return RepResult.repResult(0, "成功", null);
    }




}
