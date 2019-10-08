package com.nbcb.myron.bsen.controller;

import com.alibaba.fastjson.JSONObject;
import com.nbcb.myron.bsen.service.BsenService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("bs")
public class BsenController {

    @Autowired
    private BsenService bsenService;

    /**
     * @date:2019/1/2
     * @time:11:47
     * @description:
     */
    @PostMapping("register")
    public JSONObject register(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入用户注册" + paramsMap);
        JSONObject response = bsenService.register(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/register")
    public JSONObject register_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入用户注册" + paramsMap);
        JSONObject response = bsenService.register(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @GetMapping("indexdata")
    public JSONObject getIndexData() {
        log.info("##进入bsen获取首页数据##");
        JSONObject response = bsenService.getIndexData();
        log.info("##response: " + response);
        return response;
    }

    @GetMapping("/test/indexdata")
    public JSONObject getIndexData_() {
        log.info("##进入bsen获取首页数据##");
        JSONObject response = bsenService.getIndexData();
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("productlists")
    public JSONObject getProductLists(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen商品列表##paramsMap: " + paramsMap);
        JSONObject response = bsenService.getProductLists(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/productlists")
    public JSONObject getProductLists_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen商品列表##paramsMap: " + paramsMap);
        JSONObject response = bsenService.getProductLists(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("detail")
    public JSONObject getDetailData(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen获取详情页数据##paramsMap: " + paramsMap);
        JSONObject response = bsenService.getDetailData(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/detail")
    public JSONObject getDetailData_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen获取详情页数据##paramsMap: " + paramsMap);
        JSONObject response = bsenService.getDetailData(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @GetMapping("cutproductslist")
    public JSONObject getCutProductsList() {
        log.info("##进入bsen获取全部砍价商品##");
        JSONObject response = bsenService.getCutProductsList();
        log.info("##response: " + response);
        return response;
    }

    @GetMapping("/test/cutproductslist")
    public JSONObject getCutProductsList_() {
        log.info("##进入bsen获取全部砍价商品##");
        JSONObject response = bsenService.getCutProductsList();
        log.info("##response: " + response);
        return response;
    }

    @GetMapping("searchproducts")
    public JSONObject getSearchProducts(String keyWord) {
        log.info("##进入bsen搜索商品## 参数keyWord: " + keyWord);
        JSONObject response = bsenService.getSearchProducts(keyWord);
        log.info("##response: " + response);
        return response;
    }

    @GetMapping("/test/searchproducts")
    public JSONObject getSearchProducts_(String keyWord) {
        log.info("##进入bsen搜索商品## 参数keyWord: " + keyWord);
        JSONObject response = bsenService.getSearchProducts(keyWord);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("dynamics")
    public JSONObject getDynamics(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen获取动态" + paramsMap);
        JSONObject response = bsenService.getDynamics(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/dynamics")
    public JSONObject getDynamics_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen获取动态" + paramsMap);
        JSONObject response = bsenService.getDynamics(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("adddynamicdesc")
    public JSONObject adddynamicdesc(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen添加动态内容##desc: " + paramsMap);
        JSONObject response = bsenService.adddynamicdesc(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/adddynamicdesc")
    public JSONObject adddynamicdesc_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen添加动态内容##desc: " + paramsMap);
        JSONObject response = bsenService.adddynamicdesc(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("adddynamicimg")
    public JSONObject adddynamicimg(@RequestParam(value = "image") MultipartFile file) throws Exception {
        log.info("##进入bsen添加动态的图片附件##");
        JSONObject response = bsenService.adddynamicimg(file);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/adddynamicimg")
    public JSONObject adddynamicimg_(@RequestParam(value = "image") MultipartFile file) throws Exception {
        log.info("##进入bsen添加动态的图片附件##");
        JSONObject response = bsenService.adddynamicimg(file);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("dynamicdetail")
    public JSONObject dynamicdetail(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen获取动态详情" + paramsMap);
        JSONObject response = bsenService.dynamicdetail(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/dynamicdetail")
    public JSONObject dynamicdetail_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入bsen获取动态详情" + paramsMap);
        JSONObject response = bsenService.dynamicdetail(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("updatedz")
    public JSONObject updateDz(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入更新动态点赞" + paramsMap);
        JSONObject response = bsenService.updateDz(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/updatedz")
    public JSONObject updateDz_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入更新动态点赞" + paramsMap);
        JSONObject response = bsenService.updateDz(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("addcomment")
    public JSONObject addComment(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入添加店铺动态评论" + paramsMap);
        JSONObject response = bsenService.addComment(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/addcomment")
    public JSONObject addComment_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入添加店铺动态评论" + paramsMap);
        JSONObject response = bsenService.addComment(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("addshoppingcart")
    public JSONObject addShoppingCart(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入添加商品到购物车" + paramsMap);
        JSONObject response = bsenService.addShoppingCart(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/addshoppingcart")
    public JSONObject addShoppingCart_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入添加商品到购物车" + paramsMap);
        JSONObject response = bsenService.addShoppingCart(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("getshoppingcartpro")
    public JSONObject getShoppingCartPro(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入获取购物车产品详情" + paramsMap);
        JSONObject response = bsenService.getShoppingCartPro(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/getshoppingcartpro")
    public JSONObject getShoppingCartPro_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入获取购物车产品详情" + paramsMap);
        JSONObject response = bsenService.getShoppingCartPro(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("getshoppingcartlist")
    public JSONObject getShoppingCartList(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入获取购物车商品列表" + paramsMap);
        JSONObject response = bsenService.getShoppingCartList(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/getshoppingcartlist")
    public JSONObject getShoppingCartList_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入获取购物车商品列表" + paramsMap);
        JSONObject response = bsenService.getShoppingCartList(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("updateshoppingcartpronum")
    public JSONObject updateShoppingCartproNum(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入更新购物车商品数量" + paramsMap);
        JSONObject response = bsenService.updateShoppingCartproNum(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/updateshoppingcartpronum")
    public JSONObject updateShoppingCartproNum_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入更新购物车商品数量" + paramsMap);
        JSONObject response = bsenService.updateShoppingCartproNum(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("myinfo")
    public JSONObject myinfo(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入获取我的信息" + paramsMap);
        JSONObject response = bsenService.myInfo(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("/test/myinfo")
    public JSONObject myinfo_(@RequestBody Map<String, Object> paramsMap) {
        log.info("##进入获取我的信息" + paramsMap);
        JSONObject response = bsenService.myInfo(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("prepayment")
    public Map<String, Object> prepayment(HttpServletRequest request, @RequestBody Map<String, Object> paramsMap) {
        log.info("##进入预支付统一下单##");
        Map<String, Object> response = bsenService.prepayment(request, paramsMap);
        log.info("微信最后拉起支付页需要的response: " + response);
        return response;
    }

    @PostMapping("updateorderinfo")
    public JSONObject updateOrderInfo(@RequestBody Map<String, Object> paramsMap) {
        log.info("##更新订单状态: " + paramsMap);
        JSONObject response = bsenService.updateOrderInfo(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("myorderlist")
    public JSONObject myOrderList(@RequestBody Map<String, Object> paramsMap) {
        log.info("##我的订单列表: " + paramsMap);
        JSONObject response = bsenService.myOrderList(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("myorderinfo")
    public JSONObject myOrderInfo(@RequestBody Map<String, Object> paramsMap) {
        log.info("##我的订单详情: " + paramsMap);
        JSONObject response = bsenService.myOrderInfo(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    /**
     * @date:2019/1/9
     * @time:13:46
     * @description:对已支付订单添加评论
     */
    @PostMapping("addorderevaluate")
    public JSONObject addOrderEvaluate(@RequestBody Map<String, Object> paramsMap) {
        log.info("##对我的订单添加评价: " + paramsMap);
        JSONObject response = bsenService.addOrderEvaluate(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    /**
     * @date:2019/1/10
     * @time:15:22
     * @description:查看订单物流
     */
    @PostMapping("looksgoodsflow")
    public JSONObject looksGoodsFlow(@RequestBody Map<String, Object> paramsMap) {
        log.info("##查看物流信息: " + paramsMap);
        JSONObject response = bsenService.looksGoodFlow(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("searchflowcompany")
    public JSONObject searchFlowCompany(@RequestBody Map<String, Object> paramsMap) {
        log.info("##搜索物流公司信息: " + paramsMap);
        JSONObject response = bsenService.searchFlowCompany(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("addorderwuliuinfo")
    public JSONObject addOrderWuLiuInfo(@RequestBody Map<String, Object> paramsMap) {
        log.info("##添加物流公司信息: " + paramsMap);
        JSONObject response = bsenService.addOrderWuLiuInfo(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("bsenkfts")
    public String bsenKFTS(@RequestBody Map<String, Object> paramsMap) {
        log.info("##小程序用户消息##" + paramsMap);
        boolean response = bsenService.addUserMessage(paramsMap);
        log.info("##response: " + response);
        if (response) {
            return "success";
        }
        return null;
    }

    @PostMapping("chatlist")
    public JSONObject chatList(@RequestBody Map<String, Object> paramsMap) {
        log.info("##小程序聊天列表##" + paramsMap);
        JSONObject response = bsenService.chatList(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("chatdetail")
    public JSONObject chatDetail(@RequestBody Map<String, Object> paramsMap) {
        log.info("##小程序聊天详情##" + paramsMap);
        JSONObject response = bsenService.chatDetail(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    @PostMapping("replyusermsg")
    public JSONObject replayUser(@RequestBody Map<String, Object> paramsMap) {
        log.info("##小程序回复聊天##" + paramsMap);
        JSONObject response = bsenService.replyUserMsg(paramsMap);
        log.info("##response: " + response);
        return response;
    }

    public static void main(String[] args) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("\'content\'","{\'content\':\'阿斯蒂芬\'}");
//        map.put("\'date\'",1548826871);
//        map.put("\'type\'","\'text\'");
//        map.put("\'uId\'","\'oFmnm5dqDVYCcX3RJKXjSlVdqyHw\'");
//
//        System.out.println(JSONObject.parseObject(map.toString().replace("=",":")));
        String amountMoney = "2800.00";
        String a =Double.valueOf(amountMoney).intValue()*100+"";
        System.out.println(a);
    }

}