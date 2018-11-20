package com.nbcb.myron.bsen.mapper;

import com.nbcb.myron.bsen.module.*;

import java.util.List;
import java.util.Map;

public interface BsenDaoMapper {

    DictEntity getHttpAddress(Map<String,Object> params);

    List<ImageEntity> getImageEntityList();

    List<CutProduct> getBestCutProducts();

    List<CutProduct> getCutProductsList();

    List<BoutiqueProduct> getBoutiqueProducts();

    List<ProductListEntity> getProductLists(Map<String,Object> params);

    List<ProductListEntity> getSearchProducts(Map<String,Object> params);

    Product getDetail(String id);

    List<Dynamic>  getDynamics(Map<String,Object> params);

}
