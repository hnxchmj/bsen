package com.nbcb.myron.bsen.dao;

import com.nbcb.myron.bsen.module.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BsenDao {

    DictEntity getHttpAddress(Map<String,Object> params);

    List<ImageEntity> getImageEntityList();

    List<CutProduct> getBestCutProducts();

    List<CutProduct> getCutProductsList();

    List<BoutiqueProduct> getBoutiqueProducts();

    List<ProductListEntity> getProductLists(Map<String,Object> params);

    List<ProductListEntity> getSearchProducts(Map<String,Object> params);

    Product getDetail(String id);

    List<Dynamic>  getDynamics(Map<String,Object> params);

    Integer  addDynamicDesc(Map<String,Object> params);

    Integer  selectDynamicNum();

    Integer  addDynamicImgs(Map<String,Object> params);

    Dynamic  dynamicDetail(Map<String,Object> params);

    void  updataDynamicsBrowseNum(Map<String,Object> params);

    User  selectUser(Map<String,Object> params);

    Integer  insertNewUser(Map<String,Object> params);

    Integer  updatePlus(Map<String,Object> params);

    Integer  updateMinus(Map<String,Object> params);

    Integer  insertComment(Map<String,Object> params);

    Integer  selectUserComment(Map<String,Object> params);

    Integer  addProduct(Map<String,Object> params);

    Integer  selectCartProductsCounts();

    OrderInfo  selectCartProducts(Map<String,Object> params);

    List<OrderInfo>  selectCartProductList(Map<String,Object> params);


}
