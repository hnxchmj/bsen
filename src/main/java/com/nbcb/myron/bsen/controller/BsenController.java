package com.nbcb.myron.bsen.controller;

import com.nbcb.myron.bsen.mapper.BsenDaoMapper;
import com.nbcb.myron.bsen.module.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("bs")
public class BsenController {

    private final Logger logger = LoggerFactory.getLogger(BsenController.class);

    @Autowired
    private BsenDaoMapper bsenDaoMapper;

    @GetMapping("indexdata")
    public JSONObject getIndexData() {
        logger.info("##进入bsen获取首页数据##");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject data = new JSONObject();
        //获取http协议地址
        DictEntity address = bsenDaoMapper.getHttpAddress("bsen","localAddress");
        String httpStr = address.getIp();

        //获取首页头部轮播图数据
        List<ImageEntity> iEntityList = bsenDaoMapper.getImageEntityList();
        Iterator it = iEntityList.iterator();
        while (it.hasNext()){
            ImageEntity entity = (ImageEntity)it.next();
            String imgPath = httpStr+entity.getImgPath();
            entity.setImgPath(imgPath);
        }

        data.put("headimgswiper", iEntityList);

        //获取砍价商品信息
        List<CutProduct> cutProducts = bsenDaoMapper.getCutProducts();
        Iterator ite = cutProducts.iterator();
        while (ite.hasNext()){
            CutProduct entity = (CutProduct)ite.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("cutProducts", cutProducts);

        //获取精品推荐数据
        List<BoutiqueProduct> boutiqueProducts = bsenDaoMapper.getBoutiqueProducts();
        Iterator iter = boutiqueProducts.iterator();
        while (iter.hasNext()){
            BoutiqueProduct entity = (BoutiqueProduct)iter.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("boutiqueProducts", boutiqueProducts);

        //查询结果数据封装
        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }

    @GetMapping("productlists")
    public JSONObject getProductLists(String classifyId) {
        logger.info("##进入bsen商品列表##");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject data = new JSONObject();
        //获取商品列表
        List<ProductList> prcItems = bsenDaoMapper.getProductLists(classifyId);
        data.put("prcItems", prcItems);

        //封装返回数据
        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }

//    @GetMapping("detail")
//    public JSONObject getDetailData(@RequestParam String id ) {
//        logger.info("##进入bsen获取详情页数据##");
//        Map<String, Object> resultMap = new HashMap<>();
//        JSONObject data = new JSONObject();
//        //获取详情页数据
//        Product product = bsenDaoMapper.getDetail(id);
//        data.put("productInfo", product);
//        //获取店铺信息
//        StoreInfo storeInfo = bsenDaoMapper.getStoreInfo();
//        data.put("storeInfo", storeInfo);
//        resultMap.put("data", data);
//        resultMap.put("code", "0000");
//        resultMap.put("msg", "success");
//        JSONObject response = JSONObject.fromObject(resultMap);
//        logger.info("##response: " + response.toString());
//        return response;
//    }
//
//    @GetMapping("evaluate")
//    public JSONObject getProductEvaluate(@RequestParam String id ) {
//        logger.info("##进入bsen获取详情页用户评价##");
//        Map<String, Object> resultMap = new HashMap<>();
//        JSONObject data = new JSONObject();
//        //获取详情页评价
//        List<ProductEvaluateExt> evaluateArr = bsenDaoMapper.getEvaluate(id);
//        data.put("evaluate", evaluateArr);
//
//        //封装返回数据
//        resultMap.put("data", data);
//        resultMap.put("code", "0000");
//        resultMap.put("msg", "success");
//        JSONObject response = JSONObject.fromObject(resultMap);
//        logger.info("##response: " + response.toString());
//        return response;
//    }
//
}
