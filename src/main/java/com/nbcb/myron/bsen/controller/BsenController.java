package com.nbcb.myron.bsen.controller;

import com.nbcb.myron.bsen.mapper.BsenDaoMapper;
import com.nbcb.myron.bsen.module.ImageEntity;
import com.nbcb.myron.bsen.module.PrcEvaluate;
import com.nbcb.myron.bsen.module.Product;
import com.nbcb.myron.bsen.module.StoreInfo;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
        //获取首页头部轮播图数据
        List<ImageEntity> iEntityList = bsenDaoMapper.getImageEntityList();
        data.put("headimgswiper", iEntityList);

        //获取body图片
        ImageEntity iEntity = bsenDaoMapper.getImageEntity("4");
        data.put("bodyImgObj", iEntity);

        //获取热销商品数据
        List<Product> hotPros = bsenDaoMapper.getHotProducts("是");
        data.put("prcItems", hotPros);
        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }

    @GetMapping("detail")
    public JSONObject getDetailData(@RequestParam String id ) {
        logger.info("##进入bsen获取详情页数据##");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject data = new JSONObject();
        //获取详情页数据
        Product product = bsenDaoMapper.getDetail(id);
        data.put("productInfo", product);
        //获取店铺信息
        StoreInfo storeInfo = bsenDaoMapper.getStoreInfo();
        data.put("storeInfo", storeInfo);
        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }

    @GetMapping("evaluate")
    public JSONObject getDetailEvaluate(@RequestParam String id ) {
        logger.info("##进入bsen获取详情页用户评价##");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject data = new JSONObject();
        //获取详情页评价
        List<PrcEvaluate> evaluateArr = bsenDaoMapper.getEvaluate(id);
        data.put("evaluate", evaluateArr);

        //封装返回数据
        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }

}
