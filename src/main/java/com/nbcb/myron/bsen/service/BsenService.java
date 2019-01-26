package com.nbcb.myron.bsen.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface BsenService {

    public JSONObject register(Map<String,Object> paramsMap);

    public JSONObject getIndexData();

    public JSONObject getProductLists(Map<String,Object> params);

    public JSONObject getDetailData(Map<String,Object> params);

    public JSONObject getCutProductsList();

    public JSONObject getSearchProducts(String keyWord);

    public JSONObject getDynamics(Map<String,Object> paramsMap);

    public JSONObject adddynamicdesc(Map<String,Object> paramsMap);

    public JSONObject adddynamicimg(MultipartFile file) throws Exception;

    public JSONObject dynamicdetail(Map<String,Object> paramsMap);

    public JSONObject updateDz(Map<String,Object> paramsMap);

    public JSONObject addComment(Map<String,Object> paramsMap);

    public JSONObject addShoppingCart(Map<String,Object> paramsMap);

    public JSONObject getShoppingCartPro(Map<String,Object> paramsMap);

    public JSONObject getShoppingCartList(Map<String,Object> paramsMap);

    public JSONObject updateShoppingCartproNum(Map<String,Object> paramsMap);

    public JSONObject myinfo(Map<String,Object> paramsMap);

    public Map<String,Object> prepayment(HttpServletRequest request,Map<String, Object> paramsMap);

    public JSONObject updateOrderInfo(Map<String, Object> paramsMap);

    public JSONObject myOrderList(Map<String, Object> paramsMap);

    public JSONObject myOrderInfo(Map<String, Object> paramsMap);

    public JSONObject addOrderEvaluate(Map<String, Object> paramsMap);

    public JSONObject looksGoodFlow(Map<String, Object> paramsMap);

    public JSONObject searchFlowCompany(Map<String, Object> paramsMap);

    public JSONObject addOrderWuLiuInfo(Map<String, Object> paramsMap);
    public boolean addUserMessage(Map<String, Object> paramsMap);

    public JSONObject chatList(Map<String, Object> paramsMap);

    public JSONObject replyUserMsg(Map<String, Object> paramsMap);
}
