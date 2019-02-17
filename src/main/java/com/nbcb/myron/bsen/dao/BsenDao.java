package com.nbcb.myron.bsen.dao;

import com.alibaba.fastjson.JSONObject;
import com.nbcb.myron.bsen.module.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BsenDao {

    User  selectUser(Map<String,Object> params);

    Integer  updateUserLoginStatus(Map<String,Object> params);

    Integer  insertNewUser(Map<String,Object> params);

    DictEntity getHttpAddress(Map<String,Object> params);

    List<ImageEntity> getImageEntityList();

    List<CutProduct> getBestCutProducts();

    List<CutProduct> getCutProductsList();

    List<BoutiqueProduct> getBoutiqueProducts();

    List<ProductListEntity> getProductLists(Map<String,Object> params);

    String selectProductSoldNum(Map<String,Object> params);

    List<ProductListEntity> getProductListsJgS(Map<String,Object> params);

    List<ProductListEntity> getProductListsJgJ(Map<String,Object> params);

    List<ProductListEntity> getSearchProducts(Map<String,Object> params);

    Product getDetail(Map<String,Object> paramsMap);

    List<Dynamic>  getDynamics(Map<String,Object> params);

    Integer  addDynamicDesc(Map<String,Object> params);

    Integer  selectDynamicNum();

    Integer  addDynamicImgs(Map<String,Object> params);

    Dynamic  dynamicDetail(Map<String,Object> params);

    void  updataDynamicsBrowseNum(Map<String,Object> params);

    Integer  updatePlus(Map<String,Object> params);

    Integer  updateMinus(Map<String,Object> params);

    Integer  insertComment(Map<String,Object> params);

    Integer  selectUserComment(Map<String,Object> params);

    Integer selectProduct(Order order);

    Integer selectProduct(Map<String,Object> params);

    Integer updateProduct(Order order);

    void addProductC(Order order);

    Integer addProductO(Map<String,Object> params);

    Integer  selectCartProductsCounts(Map<String,Object> params);

    OrderInfo  selectCartProducts(Map<String,Object> params);

    OrderInfo  selectDealingProduct(Map<String,Object> params);

    List<OrderInfo>  selectCartProductList(Map<String,Object> params);

    Integer  updateProductNum(Map<String,Object> params);

    void  updateProductBrowseNum(Map<String,Object> params);

    List<User>  selectParsePsersons(Map<String,Object> params);

    UserProduct  selectDz(Map<String,Object> params);

    Integer  insertDz(Map<String,Object> params);

    Integer  updateDzP(Map<String,Object> params);

    Integer  updateDzM(Map<String,Object> params);

    public Integer updateOrderInfo(Map<String,Object> paramsMap);

    List<Order>  selectAllUserOrderlist(Map<String,Object> params);

    List<Order>  selectOrderlist(Map<String,Object> params);

    OrderInfo  adminSelectOrder(Map<String,Object> params);

    OrderInfo  selectOrder(Map<String,Object> params);

    Integer  selectUserOrderEvaluate(Map<String,Object> params);

    Integer  updateOrderEvaluate(Map<String,Object> params);

    Logistics  selecttransportCompany(Map<String,Object> params);

    List<Logistics>  selectCompanyLikeName(Map<String,Object> params);

    Integer  addOrderWuLiuInfo(Map<String,Object> params);

    Integer  selectMessageByMsgId(Map<String,Object> params);

    List<UserMessage>  selectMessageList(Map<String,Object> params);

    List<Message>  getUserMessage(Map<String,Object> params);

    Integer  addMessage(Map<String,Object> params);

    Integer  updateUserIsSession(Map<String,Object> params);

    Integer  selectToDoReplyMsgCount();

    Integer  selectToDoOrderCount();

    void  updateMsgStatus(Map<String,Object> params);
}
