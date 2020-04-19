package com.pu.purchase.mapper;

import com.pu.purchase.dto.SupplierScoreDto;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ExtSupplierScoreMapper {

    int countNum(@Param("param") Map<String,Object> map);

    List<SupplierScoreDto> querySupplier(@Param("param") Map<String,Object> map);
}
