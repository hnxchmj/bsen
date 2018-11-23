package com.nbcb.myron.bsen.controller;

import com.nbcb.myron.bsen.mapper.BsenDaoMapper;
import com.nbcb.myron.bsen.module.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

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
        Map<String,Object> params = new HashMap<>();
        params.put("dicttypeid","bsen");
        params.put("dictid","localAddress");
        DictEntity address = bsenDaoMapper.getHttpAddress(params);
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
        List<CutProduct> cutBestHotProducts = bsenDaoMapper.getBestCutProducts();
        Iterator ite = cutBestHotProducts.iterator();
        while (ite.hasNext()){
            CutProduct entity = (CutProduct)ite.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("cutProducts", cutBestHotProducts);

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

        //获取http协议地址
        Map<String,Object> params = new HashMap<>();
        params.put("dicttypeid","bsen");
        params.put("dictid","localAddress");
        DictEntity address = bsenDaoMapper.getHttpAddress(params);
        String httpStr = address.getIp();

        //获取商品列表
        Map<String, Object> sendMap = new HashMap<>();
        sendMap.put("classifyId",classifyId);
        List<ProductListEntity> prcItems = bsenDaoMapper.getProductLists(sendMap);
        Iterator it = prcItems.iterator();
        while (it.hasNext()){
            ProductListEntity entity = (ProductListEntity)it.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("prcItems", prcItems);

        //封装返回数据
        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }

    @GetMapping("detail")
    public JSONObject getDetailData(String id ) {
        logger.info("##进入bsen获取详情页数据##");

        //获取http协议地址
        Map<String,Object> params = new HashMap<>();
        params.put("dicttypeid","bsen");
        params.put("dictid","localAddress");
        DictEntity address = bsenDaoMapper.getHttpAddress(params);
        String httpStr = address.getIp();

        Map<String, Object> resultMap = new HashMap<>();
        JSONObject data = new JSONObject();
        //获取详情页数据
        Product product = bsenDaoMapper.getDetail(id);
        //获取轮播图
        List<ProductImg> imgUrls = product.getImgUrls();
        Iterator<ProductImg> it = imgUrls.iterator();
        while (it.hasNext()){
            ProductImg entity = it.next();
            String imgPath = httpStr+entity.getPath();
            entity.setPath(imgPath);
        }
        //获取详情图片
        List<ProductImg> detailImgUrls = product.getDetailImgUrls();
        Iterator<ProductImg> ite = detailImgUrls.iterator();
        while (ite.hasNext()){
            ProductImg entity = ite.next();
            String imgPath = httpStr+entity.getPath();
            entity.setPath(imgPath);
        }
        data.put("productInfo", product);

        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }

    @GetMapping("cutproductslist")
    public JSONObject getCutProductsList() {
        logger.info("##进入bsen获取全部砍价商品##");
        // 获取所有正在砍价商品
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject data = new JSONObject();

        //获取http协议地址
        Map<String,Object> params = new HashMap<>();
        params.put("dicttypeid","bsen");
        params.put("dictid","localAddress");
        DictEntity address = bsenDaoMapper.getHttpAddress(params);
        String httpStr = address.getIp();

        //获取砍价商品信息
        List<CutProduct> cutProducts = bsenDaoMapper.getCutProductsList();
        Iterator ite = cutProducts.iterator();
        while (ite.hasNext()){
            CutProduct entity = (CutProduct)ite.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("cutProducts", cutProducts);

        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }
    @GetMapping("searchproducts")
    public JSONObject getSearchProducts(String keyWord) {
        logger.info("##进入bsen搜索商品## 参数keyWord: "+keyWord);
        //获取所有正在砍价商品
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject data = new JSONObject();

        //获取http协议地址
        Map<String,Object> sendMap = new HashMap<>();
        sendMap.put("dicttypeid","bsen");
        sendMap.put("dictid","localAddress");
        DictEntity address = bsenDaoMapper.getHttpAddress(sendMap);
        String httpStr = address.getIp();

        //获取商品列表
        Map<String,Object> params = new HashMap<>();
        params.put("keyWord",keyWord);
        List<ProductListEntity> prcItems = bsenDaoMapper.getSearchProducts(params);
        Iterator it = prcItems.iterator();
        while (it.hasNext()){
            ProductListEntity entity = (ProductListEntity)it.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("prcItems", prcItems);

        //封装返回数据
        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }

    @PostMapping("dynamics")
    public JSONObject getDynamics(@RequestBody Map<String,Object> paramsMap) {
        logger.info("##进入bsen获取动态");
        //获取动态
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject data = new JSONObject();

        //获取http协议地址
        Map<String,Object> params = new HashMap<>();
        params.put("dicttypeid","bsen");
        params.put("dictid","localAddress");
        DictEntity address = bsenDaoMapper.getHttpAddress(params);
        String httpStr = address.getIp();

        //获取商品列表
        if (!paramsMap.isEmpty()){
            List<Dynamic> dynamics = bsenDaoMapper.getDynamics(paramsMap);
            Iterator it = dynamics.iterator();
            while (it.hasNext()){
                Dynamic entity = (Dynamic)it.next();
                String imgPath = httpStr+entity.getHeadImgUrl();
                entity.setHeadImgUrl(imgPath);
                List<String> contentImgs = entity.getContentImgs();
                Iterator<String> ite = contentImgs.iterator();
                List<String> imgs = new ArrayList<>();
                while (ite.hasNext()){
                    String imgUrl = ite.next();
                    imgUrl = httpStr + imgUrl;
                    imgs.add(imgUrl);
                }
                entity.setContentImgs(imgs);
            }

            data.put("dynamics", dynamics);
        }

        //封装返回数据
        resultMap.put("data", data);
        resultMap.put("code", "0000");
        resultMap.put("msg", "success");
        JSONObject response = JSONObject.fromObject(resultMap);
        logger.info("##response: " + response.toString());
        return response;
    }
    @PostMapping("adddynamic")
    public JSONObject addDynamics(@RequestParam(value="image") MultipartFile file,HttpServletRequest request) throws Exception{
        logger.info("##进入bsen添加动态##");

            JSONObject result = new JSONObject();

            System.out.print(file.getOriginalFilename());
            String filePath = "D:\\MultipartFile\\" +file.getOriginalFilename();
            file.transferTo(new File(filePath));

            String user = request.getParameter("user");
            System.out.print(user);

            result.put("code","0000");
            result.put("msg","success");
            return result;
    }
    public static String getFileType(String filename){
        if(filename.endsWith(".jpg") || filename.endsWith(".jepg")){
            return ".jpg";
        }else if(filename.endsWith(".png") || filename.endsWith(".PNG")){
            return ".png";
        } else{
            return "application/octet-stream";
        }
    }


}