package com.pu.purchase.service.impl;

import com.pu.purchase.entity.Material;
import com.pu.purchase.mapper.MaterialMapper;
import com.pu.purchase.service.IMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
