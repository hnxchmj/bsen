package com.nbcb.myron.bsen.mapper;

import com.nbcb.myron.bsen.module.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BsenDaoMapper {

    DictEntity getHttpAddress(@Param("dicttypeid") String dicttypeid,@Param("dictid") String dictid);

    List<ImageEntity> getImageEntityList();

    List<CutProduct> getBestCutProducts();

    List<CutProduct> getCutProductsList();

    List<BoutiqueProduct> getBoutiqueProducts();

    List<ProductList> getProductLists(String classifyId);

    Product getDetail(String id);

}
