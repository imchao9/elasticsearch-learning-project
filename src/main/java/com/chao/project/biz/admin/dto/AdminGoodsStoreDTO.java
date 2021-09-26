package com.chao.project.biz.admin.dto;

import lombok.Data;

@Data
public class AdminGoodsStoreDTO {
    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;
}
