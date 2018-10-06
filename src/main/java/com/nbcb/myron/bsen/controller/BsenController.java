package com.nbcb.myron.bsen.controller;

import com.nbcb.myron.bsen.mapper.BsenDaoMapper;
import com.nbcb.myron.bsen.module.ImageEntity;
import com.nbcb.myron.bsen.module.Product;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
