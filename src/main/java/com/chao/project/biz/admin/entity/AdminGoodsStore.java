package com.chao.project.biz.admin.entity;

import lombok.Data;

@Data
public class AdminGoodsStore {
    /**
     * 主键ID
     */
    private String id;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺简介
     */
    private String storeIntroduction;

    /**
     * 店铺品牌
     */
    private String storeBrand;

    /**
     * 店铺标签
     */
    private String storeTags;

    /**
     * 店铺开店时间
     */
    private String openDate;
}
