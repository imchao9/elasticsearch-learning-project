package com.chao.project.biz.admin.service;

import com.alibaba.fastjson.JSON;
import com.chao.project.biz.admin.dto.AdminGoodsStoreDTO;
import com.chao.project.biz.admin.entity.AdminGoodsStore;
import com.chao.project.biz.admin.mapper.AdminGoodsStoreMapper;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.common.dto.TableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AdminGoodsStoreServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminGoodsStoreServiceImpl.class);

    @Autowired
    private AdminGoodsStoreMapper adminGoodsStoreMapper;

    public CommonResponse<TableData<AdminGoodsStore>> getStorePageByStoreNameFromDB(
            AdminGoodsStoreDTO adminGoodsStoreDTO
    ) {
        Integer page = adminGoodsStoreDTO.getPage();
        LOGGER.info("开始调用GoodsStoreServiceImpl类getStorePageByStoreNameFromDB方法，方法使用参数[[storePageDTO]:{}]",
                JSON.toJSONString(adminGoodsStoreDTO));

        if (Objects.isNull(page) || page == 0) {
            page = 1;
        }

        TableData<AdminGoodsStore> tableData = new TableData<>();

        String storeName = adminGoodsStoreDTO.getStoreName();
        long count = adminGoodsStoreMapper.countStorePageByStoreName(storeName);
        tableData.setTotal(count);
        if (count > 0) {
            Integer size = adminGoodsStoreDTO.getSize();
            List<AdminGoodsStore> goodsStoreList = adminGoodsStoreMapper.getStorePageByStoreName(storeName, (page - 1) * size, size);
            tableData.setRows(goodsStoreList);
        }
        LOGGER.info("结束调用GoodsStoreServiceImpl类getStorePageByStoreNameFromDB方法，结果{}", JSON.toJSONString(tableData));
        return CommonResponse.success(tableData);
    }

}
