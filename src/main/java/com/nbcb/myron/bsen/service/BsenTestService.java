package com.nbcb.myron.bsen.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface BsenTestService {

    JSONObject register(Map<String, Object> paramsMap);

    JSONObject getIndexData();

    JSONObject getProductLists(Map<String, Object> params);

    JSONObject getDetailData(Map<String, Object> params);

    JSONObject getCutProductsList();

    JSONObject getSearchProducts(String keyWord);

    JSONObject getDynamics(Map<String, Object> paramsMap);

    JSONObject adddynamicdesc(Map<String, Object> paramsMap);

    JSONObject adddynamicimg(MultipartFile file) throws Exception;

    JSONObject dynamicdetail(Map<String, Object> paramsMap);

    JSONObject updateDz(Map<String, Object> paramsMap);

    JSONObject addComment(Map<String, Object> paramsMap);

    JSONObject addShoppingCart(Map<String, Object> paramsMap);

    JSONObject getShoppingCartPro(Map<String, Object> paramsMap);

    JSONObject getShoppingCartList(Map<String, Object> paramsMap);

    JSONObject updateShoppingCartproNum(Map<String, Object> paramsMap);

    JSONObject myInfo(Map<String, Object> paramsMap);

    Map<String,Object> prepayment(HttpServletRequest request, Map<String, Object> paramsMap);

    JSONObject updateOrderInfo(Map<String, Object> paramsMap);

    JSONObject myOrderList(Map<String, Object> paramsMap);

    JSONObject myOrderInfo(Map<String, Object> paramsMap);

    JSONObject addOrderEvaluate(Map<String, Object> paramsMap);

    JSONObject looksGoodFlow(Map<String, Object> paramsMap);

    JSONObject searchFlowCompany(Map<String, Object> paramsMap);

    JSONObject addOrderWuLiuInfo(Map<String, Object> paramsMap);

    boolean addUserMessage(Map<String, Object> paramsMap);

    JSONObject chatList(Map<String, Object> paramsMap);

    JSONObject chatDetail(Map<String, Object> paramsMap);

    JSONObject replyUserMsg(Map<String, Object> paramsMap);
}
