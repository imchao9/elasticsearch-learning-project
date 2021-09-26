package com.chao.project.biz.admin.controller;

import com.chao.project.biz.admin.dto.AdminGoodsStoreDTO;
import com.chao.project.biz.admin.entity.AdminGoodsStore;
import com.chao.project.biz.admin.service.AdminGoodsStoreServiceImpl;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.common.dto.TableData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/admin/goods/store")
public class AdminGoodsStoreController {
    /**
     * 后台商品店铺Service
     */
    @Autowired
    private AdminGoodsStoreServiceImpl adminGoodsStoreService;

    @PostMapping(value = "list")
    public CommonResponse<TableData<AdminGoodsStore>> getStorePageByStoreName(
            @RequestBody AdminGoodsStoreDTO adminGoodsStoreDTO
            ) {
        return adminGoodsStoreService.getStorePageByStoreNameFromDB(adminGoodsStoreDTO);
    }
}
