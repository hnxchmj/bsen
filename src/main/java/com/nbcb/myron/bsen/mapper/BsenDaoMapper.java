package com.nbcb.myron.bsen.mapper;

import com.nbcb.myron.bsen.module.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BsenDaoMapper {

    List<ImageEntity> getImageEntityList();

    ImageEntity getImageEntity(String id);

    List<Product> getAllProducts(@Param(value ="isheat" ) String isheat);

    StoreInfo getStoreInfo();

    ProductPropertyExt getDetail(String id);

    List<ProductEvaluateExt> getEvaluate(String id);

}
