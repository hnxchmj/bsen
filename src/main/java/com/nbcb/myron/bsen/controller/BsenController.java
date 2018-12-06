package com.nbcb.myron.bsen.controller;

import com.alibaba.fastjson.JSONObject;
import com.nbcb.myron.bsen.service.BsenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("bs")
public class BsenController {

    private final Logger logger = LoggerFactory.getLogger(BsenController.class);

    @Autowired
    private BsenService bsenService;


    @PostMapping("login")
    public JSONObject login(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##登录校验"+paramsMap);
        JSONObject response = bsenService.login(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @GetMapping("indexdata")
    public JSONObject getIndexData() {
        logger.info("##进入bsen获取首页数据##");
        JSONObject response = new JSONObject();
        JSONObject data = bsenService.getIndexData();
        //查询结果数据封装
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        logger.info("##response: " + response);
        return response;
    }

    @GetMapping("productlists")
    public JSONObject getProductLists(String classifyId) {
        logger.info("##进入bsen商品列表##classifyId: "+classifyId);
        JSONObject response = new JSONObject();
        JSONObject data =bsenService.getProductLists(classifyId);
        //查询结果数据封装
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("detail")
    public JSONObject getDetailData(@RequestBody Map<String,Object> paramsMap ) {
        logger.info("##进入bsen获取详情页数据##paramsMap: "+paramsMap);
        JSONObject response = new JSONObject();
        JSONObject data = bsenService.getDetailData(paramsMap);
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        logger.info("##response: " + response);
        return response;
    }

    @GetMapping("cutproductslist")
    public JSONObject getCutProductsList() {
        logger.info("##进入bsen获取全部砍价商品##");
        JSONObject response = new JSONObject();
        JSONObject data = bsenService.getCutProductsList();
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        logger.info("##response: " + response);
        return response;
    }

    @GetMapping("searchproducts")
    public JSONObject getSearchProducts(String keyWord) {
        logger.info("##进入bsen搜索商品## 参数keyWord: "+keyWord);
        JSONObject response = new JSONObject();
        JSONObject data = bsenService.getSearchProducts(keyWord);
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("dynamics")
    public JSONObject getDynamics(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##进入bsen获取动态"+paramsMap);
        JSONObject response = new JSONObject();
        JSONObject data = bsenService.getDynamics(paramsMap);
        //封装返回数据
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        logger.info("##response: " + response);
        return response;
    }
    @PostMapping("adddynamicdesc")
    public JSONObject adddynamicdesc(@RequestBody Map<String,Object> paramsMap){
        logger.info("##进入bsen添加动态内容##desc: "+paramsMap);
        JSONObject response = bsenService.adddynamicdesc(paramsMap);
        logger.info("##response: " + response);
        return response;
    }
    @PostMapping("adddynamicimg")
    public JSONObject adddynamicimg(@RequestParam(value="image") MultipartFile file) throws Exception{
        logger.info("##进入bsen添加动态的图片附件##");
        JSONObject response=bsenService.adddynamicimg(file);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("dynamicdetail")
    public JSONObject dynamicdetail(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##进入bsen获取动态详情"+paramsMap);
        JSONObject response = new JSONObject();
        JSONObject data = bsenService.dynamicdetail(paramsMap);
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("updatedz")
    public JSONObject updateDz(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##更新点赞"+paramsMap);
        JSONObject response = bsenService.updateDz(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("addcomment")
    public JSONObject addComment(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##添加店铺动态评论"+paramsMap);
        JSONObject response = bsenService.addComment(paramsMap);
        logger.info("##response: " + response);
        return response;
    }
    @PostMapping("addshoppingcart")
    public JSONObject addShoppingCart(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##添加商品到购物车"+paramsMap);
        JSONObject response = bsenService.addShoppingCart(paramsMap);
        logger.info("##response: " + response);
        return response;
    }
    @PostMapping("getshoppingcartpro")
    public JSONObject getShoppingCartPro(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##获取购物车产品详情"+paramsMap);
        JSONObject response = bsenService.getShoppingCartPro(paramsMap);
        logger.info("##response: " + response);
        return response;
    }
    @PostMapping("getshoppingcartlist")
    public JSONObject getShoppingCartList(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##获取购物车商品列表"+paramsMap);
        JSONObject response = bsenService.getShoppingCartList(paramsMap);
        logger.info("##response: " + response);
        return response;
    }
    @PostMapping("updateshoppingcartpronum")
    public JSONObject updateShoppingCartproNum(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##更新购物车商品数量"+paramsMap);
        JSONObject response = bsenService.updateShoppingCartproNum(paramsMap);
        logger.info("##response: " + response);
        return response;
    }
}