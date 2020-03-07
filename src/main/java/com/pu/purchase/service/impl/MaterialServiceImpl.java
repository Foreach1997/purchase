package com.pu.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pu.purchase.entity.Material;
import com.pu.purchase.mapper.MaterialMapper;
import com.pu.purchase.service.IMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.util.RepResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    public Object getMaterialList(){
        List<Material> materials = materialMapper.selectList(new QueryWrapper<Material>().eq("delete_Flag", "0"));
        return RepResult.repResult(0, "查询成功", materials, (long) materials.size());
    }

}
