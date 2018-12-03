package com.nbcb.myron.bsen.service.serviceImpl;

import com.nbcb.myron.bsen.dao.BsenDao;
import com.nbcb.myron.bsen.module.*;
import com.nbcb.myron.bsen.service.BsenService;
import com.nbcb.myron.bsen.utils.HttpRequest;
import com.nbcb.myron.bsen.utils.Utils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class BsenServiceImpl implements BsenService {

    private final Logger logger = LoggerFactory.getLogger(BsenServiceImpl.class);

    private String httpStr;
    @Autowired
    private BsenDao bsenDao;

    public void setHttpStr(String httpStr) {
        this.httpStr = httpStr;
    }

    @Override
    public JSONObject getIndexData() {
        JSONObject data = new JSONObject();
        //获取http协议地址
        getHttpAddress();
        //获取首页头部轮播图数据
        List<ImageEntity> iEntityList = bsenDao.getImageEntityList();
        Iterator it = iEntityList.iterator();
        while (it.hasNext()){
            ImageEntity entity = (ImageEntity)it.next();
            String imgPath = httpStr+entity.getImgPath();
            entity.setImgPath(imgPath);
        }
        data.put("headimgswiper", iEntityList);
        //获取砍价商品信息
        List<CutProduct> cutBestHotProducts = bsenDao.getBestCutProducts();
        Iterator ite = cutBestHotProducts.iterator();
        while (ite.hasNext()){
            CutProduct entity = (CutProduct)ite.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("cutProducts", cutBestHotProducts);
        //获取精品推荐数据
        List<BoutiqueProduct> boutiqueProducts = bsenDao.getBoutiqueProducts();
        Iterator iter = boutiqueProducts.iterator();
        while (iter.hasNext()){
            BoutiqueProduct entity = (BoutiqueProduct)iter.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("boutiqueProducts", boutiqueProducts);

        return data;
    }

    @Override
    public JSONObject getProductLists(String classifyId) {
        JSONObject data = new JSONObject();
        //获取http协议地址
        getHttpAddress();
        //获取商品列表
        Map<String, Object> sendMap = new HashMap<>();
        sendMap.put("classifyId",classifyId);
        List<ProductListEntity> prcItems = bsenDao.getProductLists(sendMap);
        Iterator it = prcItems.iterator();
        while (it.hasNext()){
            ProductListEntity entity = (ProductListEntity)it.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("prcItems", prcItems);
        return data;
    }

    @Override
    public JSONObject getDetailData(String id) {
        //获取http协议地址
        getHttpAddress();
        //获取详情页数据
        JSONObject data = new JSONObject();
        Product product = bsenDao.getDetail(id);
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
        //查询当前客户购物车的所有商品数量
        Integer totalCartNum = bsenDao.selectCartProductsCounts();
        data.put("counts",totalCartNum);
        return data;
    }

    @Override
    public JSONObject getCutProductsList() {
        //获取http协议地址
        getHttpAddress();
        //获取砍价商品信息
        JSONObject data = new JSONObject();
        List<CutProduct> cutProducts = bsenDao.getCutProductsList();
        Iterator ite = cutProducts.iterator();
        while (ite.hasNext()){
            CutProduct entity = (CutProduct)ite.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("cutProducts", cutProducts);
        return data;
    }

    @Override
    public JSONObject getSearchProducts(String keyWord) {
        //获取http协议地址
        getHttpAddress();
        //获取所有正在砍价商品
        JSONObject data = new JSONObject();
        //获取商品列表
        Map<String,Object> params = new HashMap<>();
        params.put("keyWord",keyWord);
        List<ProductListEntity> prcItems = bsenDao.getSearchProducts(params);
        Iterator it = prcItems.iterator();
        while (it.hasNext()){
            ProductListEntity entity = (ProductListEntity)it.next();
            String imgPath = httpStr+entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("prcItems", prcItems);
        return data;
    }

    @Override
    public JSONObject adddynamicimg(MultipartFile file) throws Exception {
        //存图片
        JSONObject map = Utils.saveFile(file);
        //添加新增动态的Id
        Integer dynamicNum = bsenDao.selectDynamicNum();
        JSONObject data = (JSONObject)map.get("data");
        data.put("did",""+dynamicNum);
        Map<String,Object> sendImgs = Utils.getMap(map);
        JSONObject response =add(sendImgs);
        return response;
    }

    @Override
    public JSONObject dynamicdetail(Map<String, Object> paramsMap) {
        JSONObject data = new JSONObject();
        //更新动态详情浏览量
        bsenDao.updataDynamicsBrowseNum(paramsMap);
        //获取http协议地址
        getHttpAddress();
        //获取动态详情
        if (!paramsMap.isEmpty()){
            Dynamic dynamicdetail = bsenDao.dynamicDetail(paramsMap);
            String imgPath = httpStr+dynamicdetail.getHeadImgUrl();
            dynamicdetail.setHeadImgUrl(imgPath);
            List<String> contentImgs = dynamicdetail.getContentImgs();
            Iterator<String> ite = contentImgs.iterator();
            List<String> imgs = new ArrayList<>();
            while (ite.hasNext()){
                String imgUrl = ite.next();
                imgUrl = httpStr + imgUrl;
                imgs.add(imgUrl);
            }
            dynamicdetail.setContentImgs(imgs);
            data.put("dynamicdetail", dynamicdetail);
        }
        return data;
    }

    @Override
    public JSONObject login(Map<String, Object> paramsMap) {

        //向微信接口校验
        String code = (String) paramsMap.get("code");
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        String params ="appid=wxb9c55e43b5fa55bd" +
                "&secret=94c6ef40162c76ebf8a9ca13b8548866" +
                "&js_code="+code+"&grant_type=authorization_code";
        String result = HttpRequest.sendGet(url,params);
        JSONObject data = JSONObject.fromObject(result);
        //查询数据库是否有该用户
        JSONObject acceptParams = new JSONObject();
        acceptParams.put("uId",data.get("openid"));
        acceptParams.put("session_key",data.get("session_key"));
        User user = bsenDao.selectUser(acceptParams);

        JSONObject response = new JSONObject();
        if (user == null){
            Integer isNum = bsenDao.insertNewUser(acceptParams);
            if (isNum == 1){
                response.put("data", acceptParams);
                response.put("code", "0000");
                response.put("msg", "添加用户成功");
            }else{
                response.put("code", "0001");
                response.put("msg", "添加用户失败");
            }
        }else{
            response.put("data", acceptParams);
            response.put("code", "0000");
            response.put("msg", "用户已存在");
        }
        return response;
    }

    @Override
    public JSONObject updateDz(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        boolean isTrue = (boolean)paramsMap.get("isTrue");
        Integer isNum =null;
        if (isTrue){
            logger.info("##赞  +1");
            isNum = bsenDao.updatePlus(paramsMap);
        }else{
            logger.info("##赞  -1");
            isNum = bsenDao.updateMinus(paramsMap);
        }
        if (isNum == 1){
            response.put("code", "0000");
            response.put("msg", "更新成功");
        }else{
            response.put("code", "0001");
            response.put("msg", "更新失败");
        }
        return response;
    }

    @Override
    public JSONObject addComment(Map<String, Object> paramsMap) {

        JSONObject response = new JSONObject();
        //检查用户是否重复评论
        Integer counts = bsenDao.selectUserComment(paramsMap);
        if (counts ==0){
            Integer oneComm = bsenDao.insertComment(paramsMap);
            if (oneComm == 1){
                response.put("code","0000");
                response.put("msg","添加成功");
            }else{
                response.put("code","0001");
                response.put("msg","添加失败");
            }
        }else{
            response.put("code","0001");
            response.put("msg","用户禁止重复评论");
        }
        return response;
    }

    @Override
    public JSONObject addShoppingCart(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();

        paramsMap.put("status",0);
        paramsMap.put("time",new Date().getTime());
        Integer one = bsenDao.addProduct(paramsMap);
        //查询当前客户购物车的所有商品数量
        Integer totalCartNum = bsenDao.selectCartProductsCounts();
        JSONObject data = new JSONObject();
        data.put("counts",totalCartNum);
        if (one == 1){
            response.put("data",data);
            response.put("code","0000");
            response.put("msg","添加成功");
        }else{
            response.put("code","0001");
            response.put("msg","添加失败");
        }
        return response;
    }

    @Override
    public JSONObject getShoppingCartPro(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        //获取http协议地址
        getHttpAddress();
        //获取动态详情
        OrderInfo orderInfo=null;
        if (!paramsMap.isEmpty()){
            orderInfo=bsenDao.selectCartProducts(paramsMap);
            String imgPath = httpStr+orderInfo.getImgUrl();
            orderInfo.setImgUrl(imgPath);
            data.put("orderInfo",orderInfo);
        }
        if (orderInfo!=null){
            response.put("data",data);
            response.put("code","0000");
            response.put("msg","获取订单成功");
        }else{
            response.put("code","0001");
            response.put("msg","获取订单失败");
        }
        return response;
    }

    @Override
    public JSONObject getShoppingCartList(Map<String, Object> paramsMap) {

        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        //获取http协议地址
        getHttpAddress();
        List<OrderInfo> productList=null;
        if (!paramsMap.isEmpty()){
            productList=bsenDao.selectCartProductList(paramsMap);
            Iterator<OrderInfo> ite = productList.iterator();
            while (ite.hasNext()){
                OrderInfo entity = ite.next();
                String imgPath = httpStr+entity.getImgUrl();
                entity.setImgUrl(imgPath);
            }
            data.put("productList",productList);
        }
        if (productList!=null){
            response.put("data",data);
            response.put("code","0000");
            response.put("msg","查询成功");
        }else{
            response.put("code","0001");
            response.put("msg","查询失败");
        }
        return response;
    }

    @Override
    public JSONObject adddynamicdesc(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        String leaveAMessage = (String)paramsMap.get("desc");
        if (leaveAMessage != null && leaveAMessage !=""){
            Map<String,Object> sendDesc = new HashMap<>();
            sendDesc.put("userId","oM78247bYUgo2bsq8ZUEAf_d-GLM");
            sendDesc.put("desc",leaveAMessage);
            sendDesc.put("time",""+new Date().getTime());
            //向数据库添加动态描述
            Integer oneDesc = bsenDao.addDynamicDesc(sendDesc);
            if (oneDesc == 1){
                response.put("code","0000");
                response.put("msg","保存成功");
            }else{
                response.put("code","0001");
                response.put("msg","保存失败");
            }
        }else{
            response.put("code","0001");
            response.put("msg","保存失败");
        }
        return response;
    }

    @Override
    public JSONObject getDynamics(Map<String, Object> paramsMap) {
        JSONObject data = new JSONObject();
        //更新浏览量(传一个空的map对象占位
        boolean isTrue =(boolean)paramsMap.get("isTrue");
        if (isTrue){
            bsenDao.updataDynamicsBrowseNum(paramsMap);
        }
        //获取http协议地址
        getHttpAddress();
        //获取商品列表
        if (!paramsMap.isEmpty()){
            List<Dynamic> dynamics = bsenDao.getDynamics(paramsMap);
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
        return data;
    }

    private void getHttpAddress() {
        Map<String,Object> params = new HashMap<>();
        params.put("dicttypeid","bsen");
        params.put("dictid","localAddress");
        DictEntity dictEntity = bsenDao.getHttpAddress(params);
        String httpStr = dictEntity.getIp();
        setHttpStr(httpStr);
    }

    private JSONObject add(Map<String,Object> sendImgs){
        JSONObject result = new JSONObject();
        if (sendImgs != null){
            Integer oneImg = bsenDao.addDynamicImgs(sendImgs);
            if (oneImg==1){
                result.put("code","0000");
                result.put("msg","图片保存成功");
            }else{
                result.put("code","0002");
                result.put("msg","图片保存失败");
            }
        }else{
            result.put("code","0001");
            result.put("msg","图片上传失败");
        }
        return result;
    }

}
