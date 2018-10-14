package com.nbcb.myron.bsen.mapper;

import com.nbcb.myron.bsen.module.ImageEntity;
import com.nbcb.myron.bsen.module.PrcEvaluate;
import com.nbcb.myron.bsen.module.Product;
import com.nbcb.myron.bsen.module.StoreInfo;

import java.util.List;

public interface BsenDaoMapper {

    List<ImageEntity> getImageEntityList();

    ImageEntity getImageEntity(String id);

    List<Product> getHotProducts(String isheat);

    StoreInfo getStoreInfo();

    Product getDetail(String id);

    List<PrcEvaluate> getEvaluate(String id);


}
