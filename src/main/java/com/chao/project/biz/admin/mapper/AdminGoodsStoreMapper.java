package com.chao.project.biz.admin.mapper;

import com.chao.project.biz.admin.entity.AdminGoodsStore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangjunchao
 */
@Mapper
public interface AdminGoodsStoreMapper {
    /**
     * 根据店铺名称分页查询店铺信息总数
     *
     * @param storeName 店铺名称
     * @return 结果
     */
    Integer countStorePageByStoreName(@Param("storeName") String storeName);

    /**
     * 根据店铺名称分页查询店铺信息
     *
     * @param storeName 店铺名称
     * @param start     起始位置
     * @param size      每页大小
     * @return 结果
     */
    List<AdminGoodsStore> getStorePageByStoreName(@Param("storeName") String storeName,
                                                  @Param("start") Integer start,
                                                  @Param("size") Integer size);
}
