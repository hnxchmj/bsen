package com.nbcb.myron.bsen.mapper;

import com.nbcb.myron.bsen.module.ImageEntity;
import com.nbcb.myron.bsen.module.Product;

import java.util.List;

public interface BsenDaoMapper {

    List<ImageEntity> getImageEntityList();

    ImageEntity getImageEntity(String id);

    List<Product> getHotProducts(String isheat);

}
