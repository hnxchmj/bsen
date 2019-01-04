package com.nbcb.myron.bsen.controller;

import com.alibaba.fastjson.JSONObject;
import com.nbcb.myron.bsen.service.BsenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author:黄孟军
 * @date:2019/1/2
 * @description:佰森小程序页面主控制类
 */
@RestController
@RequestMapping("bs")
public class BsenController {

    private final Logger logger = LoggerFactory.getLogger(BsenController.class);

    @Autowired
    private BsenService bsenService;

    /**
     * @date:2019/1/2
     * @time:11:47
     * @description:
     */
    @PostMapping("register")
    public JSONObject register(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入用户注册" + paramsMap);
        JSONObject response = bsenService.register(paramsMap);
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

    @PostMapping("productlists")
    public JSONObject getProductLists(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入bsen商品列表##paramsMap: " + paramsMap);
        JSONObject response = bsenService.getProductLists(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("detail")
    public JSONObject getDetailData(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入bsen获取详情页数据##paramsMap: " + paramsMap);
        JSONObject response = bsenService.getDetailData(paramsMap);
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
        logger.info("##进入bsen搜索商品## 参数keyWord: " + keyWord);
        JSONObject response = new JSONObject();
        JSONObject data = bsenService.getSearchProducts(keyWord);
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("dynamics")
    public JSONObject getDynamics(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入bsen获取动态" + paramsMap);
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
    public JSONObject adddynamicdesc(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入bsen添加动态内容##desc: " + paramsMap);
        JSONObject response = bsenService.adddynamicdesc(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("adddynamicimg")
    public JSONObject adddynamicimg(@RequestParam(value = "image") MultipartFile file) throws Exception {
        logger.info("##进入bsen添加动态的图片附件##");
        JSONObject response = bsenService.adddynamicimg(file);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("dynamicdetail")
    public JSONObject dynamicdetail(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入bsen获取动态详情" + paramsMap);
        JSONObject response = new JSONObject();
        JSONObject data = bsenService.dynamicdetail(paramsMap);
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("updatedz")
    public JSONObject updateDz(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入更新动态点赞" + paramsMap);
        JSONObject response = bsenService.updateDz(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("addcomment")
    public JSONObject addComment(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入添加店铺动态评论" + paramsMap);
        JSONObject response = bsenService.addComment(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("addshoppingcart")
    public JSONObject addShoppingCart(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入添加商品到购物车" + paramsMap);
        JSONObject response = bsenService.addShoppingCart(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("getshoppingcartpro")
    public JSONObject getShoppingCartPro(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入获取购物车产品详情" + paramsMap);
        JSONObject response = bsenService.getShoppingCartPro(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("getshoppingcartlist")
    public JSONObject getShoppingCartList(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入获取购物车商品列表" + paramsMap);
        JSONObject response = bsenService.getShoppingCartList(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("updateshoppingcartpronum")
    public JSONObject updateShoppingCartproNum(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入更新购物车商品数量" + paramsMap);
        JSONObject response = bsenService.updateShoppingCartproNum(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("myinfo")
    public JSONObject myinfo(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入获取我的信息" + paramsMap);
        JSONObject response = bsenService.myinfo(paramsMap);
        logger.info("##response: " + response);
        return response;
    }

    @PostMapping("prepayment")
    public Map<String, Object> prepayment(HttpServletRequest request, @RequestBody Map<String, Object> paramsMap) {
        logger.info("##进入预支付统一下单##");
        Map<String, Object> response = bsenService.prepayment(request, paramsMap);
        logger.info("微信最后拉起支付页需要的response: " + response);
        return response;
    }

    @PostMapping("updateorderinfo")
    public JSONObject updateOrderInfo(@RequestBody Map<String, Object> paramsMap) {
        logger.info("##更新订单状态: " + paramsMap);
        JSONObject response = bsenService.updateOrderInfo(paramsMap);
        logger.info("##response: " + response);
        return null;
    }
}