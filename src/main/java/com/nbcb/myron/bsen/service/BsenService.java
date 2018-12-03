package com.nbcb.myron.bsen.service;

import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface BsenService {

    public JSONObject getIndexData();

    public JSONObject getProductLists(String classifyId);

    public JSONObject getDetailData(String id );

    public JSONObject getCutProductsList();

    public JSONObject getSearchProducts(String keyWord);

    public JSONObject getDynamics(Map<String,Object> paramsMap);

    public JSONObject adddynamicdesc(Map<String,Object> paramsMap);

    public JSONObject adddynamicimg(MultipartFile file) throws Exception;

    public JSONObject dynamicdetail(Map<String,Object> paramsMap);

    public JSONObject login(Map<String,Object> paramsMap);

    public JSONObject updateDz(Map<String,Object> paramsMap);

    public JSONObject addComment(Map<String,Object> paramsMap);

    public JSONObject addShoppingCart(Map<String,Object> paramsMap);

    public JSONObject getShoppingCartPro(Map<String,Object> paramsMap);

    public JSONObject getShoppingCartList(Map<String,Object> paramsMap);
}
