package com.pu.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.config.BizException;
import com.pu.purchase.dto.PurchaseFormDto;
import com.pu.purchase.entity.*;
import com.pu.purchase.mapper.*;
import com.pu.purchase.service.IPurchaseFormService;
import com.pu.purchase.util.*;
import com.pu.purchase.vo.PurchaseFormVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
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
@Slf4j
public class PurchaseFormServiceImpl extends ServiceImpl<PurchaseFormMapper, PurchaseForm> implements IPurchaseFormService {


    @Resource
    private PurchaseFormMapper purchaseFormMapper;
    @Resource
    private PurchaseDetailMapper purchaseDetailMapper;
    @Resource
    private SupplierMapper supplierMapper;
    @Resource
    private SupplierScoreMapper supplierScoreMapper;
    @Resource
    private DeliverFormMapper deliverFormMapper;
    @Resource
    private NoNutil noNutil;

    public Object queryAllPurchaseForm(PurchaseFormVo purchaseFormVo) {
        LambdaQueryWrapper<PurchaseForm> queryWrapper = new LambdaQueryWrapper<PurchaseForm>()
                .eq(StringUtils.isNotBlank(purchaseFormVo.getNo()), PurchaseForm::getNo, purchaseFormVo.getNo())
                .eq(StringUtils.isNotBlank(purchaseFormVo.getName()), PurchaseForm::getName, purchaseFormVo.getName())
                .eq(null != purchaseFormVo.getStatus(), PurchaseForm::getStatus, purchaseFormVo.getStatus());

        IPage<PurchaseForm> purchaseFormPage = purchaseFormMapper.selectPage(new Page<>(purchaseFormVo.getPage(), purchaseFormVo.getLimit()), queryWrapper);
        List<PurchaseFormDto> dtoList = purchaseFormPage.getRecords().stream().map(purchaseForm -> {
            PurchaseFormDto purchaseFormDto = new PurchaseFormDto();
            BeanUtils.copyProperties(purchaseForm, purchaseFormDto);
            PurchaseDetail purchaseDetail = purchaseDetailMapper.selectOne(new QueryWrapper<PurchaseDetail>().eq("purchase_no", purchaseForm.getNo()));
            purchaseFormDto.setProductNo(purchaseDetail.getProductNo());
            purchaseFormDto.setPurchaseQuality(purchaseDetail.getPurchaseQuality());
            purchaseFormDto.setStorageQuality(purchaseDetail.getStorageQuality());
            purchaseFormDto.setPurchasePrice(purchaseDetail.getPurchasePrice());
            purchaseFormDto.setStatus(purchaseForm.getStatus().toString());
            purchaseFormDto.setUpdateDate(DateUtils.dateFrString(purchaseForm.getUpdateDate()));
            purchaseFormDto.setCreateDate(DateUtils.dateFrString(purchaseForm.getCreateDate()));
            return purchaseFormDto;
        }).collect(Collectors.toList());
        return RepResult.repResult(0, "查询成功", dtoList, (long) purchaseFormPage.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    public Object insertSelective(PurchaseFormVo record) {
        if (null == record) {
            throw new BizException("添加数据为空");
        }
        String no = noNutil.getAllNo();
        record.setNo(no);
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
        purchaseDetail.setPurchaseNo(no);
        purchaseDetail.setArriveTime(DateUtils.getLocalDateTime(record.getArriveTime()));
        purchaseDetail.setPrice(record.getPurchasePrice().divide(new BigDecimal(record.getPurchaseQuality()),2, RoundingMode.HALF_UP));
        if (1 != purchaseDetailMapper.insert(purchaseDetail)) {
            throw new BizException("添加采购单详情失败");
        }
        List<SupplierScore> supplierScores = supplierScoreMapper.selectList(new QueryWrapper<SupplierScore>().lambda()
                .eq(SupplierScore::getMaterialId,purchaseDetail.getProductNo())
                .orderByDesc(SupplierScore::getSupplierScore)
                .last("limit 3"));
        if (supplierScores.size()==0){
            throw new BizException("没有对应供应商");
        }
        for (SupplierScore score : supplierScores) {
            DeliverForm deliver = new DeliverForm();
            deliver.setSupplierId(score.getSupplierId());
            deliver.setUpdateDate(LocalDateTime.now());
            deliver.setPurchaseNo(purchaseDetail.getPurchaseNo());
            deliver.setNo("SN"+System.currentTimeMillis());
            deliverFormMapper.insert(deliver);
            Supplier supplier =  supplierMapper.selectOne(new QueryWrapper<Supplier>().lambda()
                    .eq(Supplier::getId,deliver.getSupplierId()));
            try {
                SendEmail.send(supplier.getEmail(), "http://localhost:8080/purMsg/inquiryOrder.html?no=" + deliver.getNo());
            }catch (Exception e){
                log.info(e.getMessage());
            }
        }
        return RepResult.repResult(0, "添加成功", null);
    }

    public Object updateByPrimaryKeySelective(PurchaseFormVo record) {
        if (null == record) {
            throw new BizException("修改数据为空");
        }
        if (1 != purchaseFormMapper.update(record, new QueryWrapper<PurchaseForm>().eq("no", record.getNo()))) {
            throw new BizException("修改采购单失败");
        }
        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setPurchaseNo(record.getNo());
        purchaseDetail.setProductNo(record.getProductNo());
        purchaseDetail.setPurchaseQuality(record.getPurchaseQuality());
        purchaseDetail.setPurchasePrice(record.getPurchasePrice());
        if (1 != purchaseDetailMapper.update(purchaseDetail, new QueryWrapper<PurchaseDetail>().eq("purchase_no", record.getNo()))) {
            throw new BizException("修改采购明细单失败");
        }
        return RepResult.repResult(0, "修改成功", null);
    }


    public Object deleteByPrimaryKey(String id) {
        if (null == id) {
            throw new BizException("删除id为空");
        }
        if (1 != purchaseFormMapper.delete(new QueryWrapper<PurchaseForm>().eq("no", id))) {
            throw new BizException("删除采购单失败");
        }
        if (1 != purchaseDetailMapper.delete(new QueryWrapper<PurchaseDetail>().eq("purchase_no", id))) {
            throw new BizException("删除采购明细单失败");
        }
        return RepResult.repResult(0, "删除成功", null);
    }

    public Object importPurchaseForm(MultipartFile files) {
        try {
            int begin = files.getOriginalFilename().indexOf(".");
            int last = files.getOriginalFilename().length();
            String fileType = files.getOriginalFilename().substring(begin, last);
            InputStream stream = files.getInputStream();
            Workbook wb = null;
            if (fileType.equals(".xls")) {
                wb = new HSSFWorkbook(stream);
            } else if (fileType.equals(".xlsx")) {
                wb = new XSSFWorkbook(stream);
            } else {
                return RepResult.repResult(0, "请导入excel文件：.xlsx", null);
            }
            Sheet sheet1 = wb.getSheetAt(0);
            int totalRows = sheet1.getPhysicalNumberOfRows();

            if (sheet1.getLastRowNum() == 0 && sheet1.getPhysicalNumberOfRows() == 0) {
                return RepResult.repResult(0, "您的excel文件为空", null);
            } else {
                for (int r = 1; r < totalRows; r++) {
                    PurchaseFormVo purchaseFormVo = new PurchaseFormVo();
                    Row row = sheet1.getRow(r);
                    purchaseFormVo.setName(Double.valueOf(String.valueOf(row.getCell(0) == null ? "" : row.getCell(0).toString())).intValue()+"");
                    purchaseFormVo.setStatus(Double.valueOf(String.valueOf(row.getCell(1) == null ? "" : row.getCell(1))).intValue());
                    purchaseFormVo.setProductNo(Double.valueOf(String.valueOf(row.getCell(2) == null ? "" : row.getCell(2))).intValue()+"");
                    purchaseFormVo.setPurchaseQuality(Double.valueOf(String.valueOf(row.getCell(3) == null ? "" : row.getCell(3))).intValue());
                    purchaseFormVo.setPurchasePrice(new BigDecimal(Double.valueOf(String.valueOf(row.getCell(4) == null ? "" : row.getCell(4)))));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    purchaseFormVo.setArriveTime(simpleDateFormat.parse(String.valueOf(row.getCell(5) == null ? "" : row.getCell(5).toString())));
                    insertSelective(purchaseFormVo);
                }
            }
        } catch (Exception e) {
            return RepResult.repResult(0, e.getMessage(), null);
        }
        return RepResult.repResult(0, "导入成功", null);
    }


}
