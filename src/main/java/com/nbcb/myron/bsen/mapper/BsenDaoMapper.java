package com.nbcb.myron.bsen.mapper;

import com.nbcb.myron.bsen.module.*;

import java.util.List;

public interface BsenDaoMapper {

    List<ImageEntity> getImageEntityList();

    ImageEntity getImageEntity(String id);

    List<Product> getHotProducts(String isheat);

    StoreInfo getStoreInfo();

    Product getDetail(String id);

    List<PrcEvaluate> getEvaluate(String id);

}
