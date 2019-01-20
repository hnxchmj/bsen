package com.nbcb.myron.bsen.service.serviceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nbcb.myron.bsen.common.HttpRequest;
import com.nbcb.myron.bsen.common.MD5;
import com.nbcb.myron.bsen.common.MyRedisCache;
import com.nbcb.myron.bsen.dao.BsenDao;
import com.nbcb.myron.bsen.module.*;
import com.nbcb.myron.bsen.service.BsenService;
import com.nbcb.myron.bsen.utils.KdniaoTrackQueryAPI;
import com.nbcb.myron.bsen.utils.Utils;
import com.nbcb.myron.bsen.utils.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class BsenServiceImpl implements BsenService {

    private final Logger logger = LoggerFactory.getLogger(BsenServiceImpl.class);

    private MyRedisCache myRedisCache;
    private static final long EXPIRE_TIME_IN_MINUTES = 60; // redis过期时间
    private String httpStr;

    @Autowired
    private BsenDao bsenDao;

    public void setHttpStr(String httpStr) {
        this.httpStr = httpStr;
    }

    private MyRedisCache getRedisTemplate() {
        if (myRedisCache == null) {
            myRedisCache = new MyRedisCache("cookies");
        }
        return myRedisCache;
    }

    @Override
    public JSONObject register(Map<String, Object> paramsMap) {

        //向微信接口校验
        String code = (String) paramsMap.get("code");
        String nickName = (String) paramsMap.get("nickName");
        String avatarUrl = (String) paramsMap.get("avatarUrl");
        Integer gender = (Integer) paramsMap.get("gender");

        String appid = "wx0cc8ab70b74f6211";
        String secret = "be78809d3f2f4447ead9735a574cba85";
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        String params = "appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        String result = HttpRequest.sendGet(url, params);
        JSONObject data = JSONObject.parseObject(result);
        logger.info("微信返回数据data" + data);
        JSONObject response = new JSONObject();

        if (data.get("openid") != null) {
            //对用户session_key 进行加密处理
            String session_key = (String) data.get("session_key");//密钥key
            String openid = (String) data.get("openid");
            String loginState = null;
            try {
                loginState = MD5.md5(openid, session_key);//维护后的登录态
            } catch (Exception e) {
                logger.info("9999", e);
            }
            JSONObject acceptParams = new JSONObject();
            acceptParams.put("uId", data.get("openid"));
            //查询该用户是否存在
            User user = bsenDao.selectUser(acceptParams);

            acceptParams.put("gender", gender);
            acceptParams.put("avatarUrl", avatarUrl);
            acceptParams.put("nickName", nickName);
            acceptParams.put("session_key", session_key);
            acceptParams.put("login_state", loginState);
            if (user == null) {//添加新用户
                Integer isNum = bsenDao.insertNewUser(acceptParams);
                if (isNum == 1) {
                    acceptParams.clear();
                    acceptParams.put("login_state", loginState);
                    response.put("data", acceptParams);
                    response.put("code", "0000");
                    response.put("msg", "添加用户成功");
                } else {
                    response.put("code", "0001");
                    response.put("msg", "添加用户失败");
                }
            } else {//更新已有用户
                Integer isNum = bsenDao.updateUserLoginStatus(acceptParams);
                if (isNum == 1) {
                    response.put("code", "0000");
                    response.put("msg", "用户已存在,更新信息成功");
                } else {
                    response.put("code", "0001");
                    response.put("msg", "用户已存在,更新信息失败");
                }
            }
            //存入redis用户登录态loginState信息
            acceptParams.clear();
            acceptParams.put("uId", data.get("openid"));
            myRedisCache = getRedisTemplate();
            logger.info("###存入redis用户参数" + acceptParams.toJSONString());
            myRedisCache.putObject(loginState, acceptParams);
            acceptParams.clear();
            acceptParams.put("login_state", loginState);
            response.put("data", acceptParams);
        } else {
            response.put("code", "0001");
            response.put("msg", "微信服务器返回数据不成功");
        }
        return response;
    }

    @Override
    public JSONObject getIndexData() {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        //获取http协议地址
        getHttpAddress();
        //获取首页头部轮播图数据
        List<ImageEntity> iEntityList = bsenDao.getImageEntityList();
        Iterator it = iEntityList.iterator();
        while (it.hasNext()) {
            ImageEntity entity = (ImageEntity) it.next();
            String imgPath = httpStr + entity.getImgPath();
            entity.setImgPath(imgPath);
        }
        data.put("headimgswiper", iEntityList);
        //获取砍价商品信息
        List<CutProduct> cutBestHotProducts = bsenDao.getBestCutProducts();
        Iterator ite = cutBestHotProducts.iterator();
        while (ite.hasNext()) {
            CutProduct entity = (CutProduct) ite.next();
            String imgPath = httpStr + entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("cutProducts", cutBestHotProducts);
        //获取精品推荐数据
        List<BoutiqueProduct> boutiqueProducts = bsenDao.getBoutiqueProducts();
        Iterator iter = boutiqueProducts.iterator();
        while (iter.hasNext()) {
            BoutiqueProduct entity = (BoutiqueProduct) iter.next();
            String imgPath = httpStr + entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("boutiqueProducts", boutiqueProducts);
        //查询结果数据封装
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        return response;
    }

    @Override
    public JSONObject getProductLists(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        //获取http协议地址
        getHttpAddress();
        //获取商品列表
        List<ProductListEntity> prcItems = null;
        if (paramsMap.get("cond") == null) {
            prcItems = bsenDao.getProductLists(paramsMap);
        } else {
            String conf = (String) paramsMap.get("cond");
            if ("zh".equals(conf)) {

            } else if ("zr".equals(conf)) {

            } else if ("xp".equals(conf)) {

            } else {//价格
                if (conf.indexOf("true") > 0) {//升
                    prcItems = bsenDao.getProductListsJgS(paramsMap);
                } else {//降
                    prcItems = bsenDao.getProductListsJgJ(paramsMap);
                }
            }
        }
        Iterator it = prcItems.iterator();
        while (it.hasNext()) {
            ProductListEntity entity = (ProductListEntity) it.next();
            //查询单个商品的已售数量
            String pid = entity.getId();
            Map<String, Object> params = new HashMap<>();
            params.put("pid", pid);
            String soldNum = bsenDao.selectProductSoldNum(params);
            entity.setSoldNum(soldNum);
            String imgPath = httpStr + entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("prcItems", prcItems);
        //查询结果数据封装
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        return response;
    }

    @Override
    public JSONObject getDetailData(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("userId", uId);
        }
//          else{
//            response.put("code","0001");
//            response.put("msg","该用户不存在");
//            return response;
//        }
        boolean isUpdateDz = (boolean) paramsMap.get("isUpdateDz");
        if (isUpdateDz) {//更新详情页点赞
            Integer isNum = null;
            UserProduct userProduct = bsenDao.selectDz(paramsMap);
            if (userProduct != null) {
                if (userProduct.getIsdz() == 1) {
                    isNum = bsenDao.updateDzM(paramsMap);//取消赞
                } else {
                    isNum = bsenDao.updateDzP(paramsMap);//添加赞
                }
            } else {
                isNum = bsenDao.insertDz(paramsMap);//添加点赞
            }
            if (isNum == 1) {
                //查询对当前商品点赞的所有人员头像
                paramsMap.remove("userId");
                List<User> usersList = bsenDao.selectParsePsersons(paramsMap);
                data.put("praiseMans", usersList);
            }
            response.put("data", data);
            response.put("code", "0000");
            response.put("msg", "更新赞成功");
        } else {//查询详情信息
            //刷新浏览记录
            bsenDao.updateProductBrowseNum(paramsMap);
            //获取http协议地址
            getHttpAddress();
            //获取详情页数据
            Product product = bsenDao.getDetail(paramsMap);
            //获取轮播图
            List<ProductImg> imgUrls = product.getImgUrls();
            Iterator<ProductImg> it = imgUrls.iterator();
            while (it.hasNext()) {
                ProductImg entity = it.next();
                String imgPath = httpStr + entity.getPath();
                entity.setPath(imgPath);
            }
            //获取详情图片
            List<ProductImg> detailImgUrls = product.getDetailImgUrls();
            Iterator<ProductImg> ite = detailImgUrls.iterator();
            while (ite.hasNext()) {
                ProductImg entity = ite.next();
                String imgPath = httpStr + entity.getPath();
                entity.setPath(imgPath);
            }
            data.put("productInfo", product);
            //查询当前客户购物车的所有商品数量
            Integer totalCartNum = bsenDao.selectCartProductsCounts(paramsMap);
            data.put("counts", totalCartNum);
            //查询对当前商品点赞的所有人员头像
            paramsMap.remove("userId");
            List<User> usersList = bsenDao.selectParsePsersons(paramsMap);
            data.put("praiseMans", usersList);
            response.put("data", data);
            response.put("code", "0000");
            response.put("msg", "查询详情成功");
        }
        return response;
    }

    @Override
    public JSONObject getCutProductsList() {
        //获取http协议地址
        getHttpAddress();
        //获取砍价商品信息
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        List<CutProduct> cutProducts = bsenDao.getCutProductsList();
        Iterator ite = cutProducts.iterator();
        while (ite.hasNext()) {
            CutProduct entity = (CutProduct) ite.next();
            String imgPath = httpStr + entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("cutProducts", cutProducts);
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        return response;
    }

    @Override
    public JSONObject getSearchProducts(String keyWord) {
        //获取所有正在砍价商品
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        //获取http协议地址
        getHttpAddress();
        //获取商品列表
        Map<String, Object> params = new HashMap<>();
        params.put("keyWord", keyWord);
        List<ProductListEntity> prcItems = bsenDao.getSearchProducts(params);
        Iterator it = prcItems.iterator();
        while (it.hasNext()) {
            ProductListEntity entity = (ProductListEntity) it.next();
            String imgPath = httpStr + entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("prcItems", prcItems);
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "success");
        return response;
    }

    @Override
    public JSONObject adddynamicimg(MultipartFile file) throws Exception {
        //存图片
        JSONObject map = Utils.saveFile(file);
        //添加新增动态的Id
        Integer dynamicNum = bsenDao.selectDynamicNum();
        JSONObject data = (JSONObject) map.get("data");
        data.put("did", "" + dynamicNum);
        Map<String, Object> sendImgs = Utils.getMap(map);
        JSONObject response = add(sendImgs);
        return response;
    }

    @Override
    public JSONObject dynamicdetail(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        //更新动态详情浏览量
        bsenDao.updataDynamicsBrowseNum(paramsMap);
        //获取http协议地址
        getHttpAddress();
        //获取动态详情
        if (!paramsMap.isEmpty()) {
            Dynamic dynamicdetail = bsenDao.dynamicDetail(paramsMap);
            String imgPath = httpStr + dynamicdetail.getHeadImgUrl();
            dynamicdetail.setHeadImgUrl(imgPath);
            List<String> contentImgs = dynamicdetail.getContentImgs();
            Iterator<String> ite = contentImgs.iterator();
            List<String> imgs = new ArrayList<>();
            while (ite.hasNext()) {
                String imgUrl = ite.next();
                imgUrl = httpStr + imgUrl;
                imgs.add(imgUrl);
            }
            dynamicdetail.setContentImgs(imgs);
            data.put("dynamicdetail", dynamicdetail);
            response.put("data", data);
            response.put("code", "0000");
            response.put("msg", "success");
        }
        return response;
    }

    @Override
    public JSONObject updateDz(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        boolean isTrue = (boolean) paramsMap.get("isTrue");
        Integer isNum = null;
        if (isTrue) {
            logger.info("##赞  +1");
            isNum = bsenDao.updatePlus(paramsMap);
        } else {
            logger.info("##赞  -1");
            isNum = bsenDao.updateMinus(paramsMap);
        }
        if (isNum == 1) {
            response.put("code", "0000");
            response.put("msg", "更新成功");
        } else {
            response.put("code", "0001");
            response.put("msg", "更新失败");
        }
        return response;
    }

    @Override
    public JSONObject addComment(Map<String, Object> paramsMap) {

        JSONObject response = new JSONObject();
        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("userId", uId);
        }
        //检查用户是否重复评论
        Integer counts = bsenDao.selectUserComment(paramsMap);
        if (counts == 0) {
            Integer oneComm = bsenDao.insertComment(paramsMap);
            if (oneComm == 1) {
                response.put("code", "0000");
                response.put("msg", "添加成功");
            } else {
                response.put("code", "0001");
                response.put("msg", "添加失败");
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "用户禁止重复评论");
        }
        return response;
    }

    /**
     * @date:2019/1/3
     * @time:13:38
     * @description:添加商品到购物车
     */
    @Override
    public JSONObject addShoppingCart(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("userId", uId);
            paramsMap.put("time", "" + new Date().getTime());
            Order order = new Order();
            order.setPid((String) paramsMap.get("id"));
            order.setTurnOver((String) paramsMap.get("turnOver"));
            order.setPrice((String) paramsMap.get("price"));
            Integer status = (Integer) paramsMap.get("status");
            order.setStatus(status);
            order.setUserId((String) paramsMap.get("userId"));
            order.setTime((String) paramsMap.get("time"));
            //添加到购物车
            if (status == 0) {
                //查询当前客户是否已有被添加的商品
                Integer count = bsenDao.selectProduct(order);
                if (count > 0) {//更新当前客户所添加的产品数量
                    Integer updateCount = bsenDao.updateProduct(order);
                    //查询当前客户购物车的所有商品数量
                    Integer totalCartNum = bsenDao.selectCartProductsCounts(paramsMap);
                    JSONObject data = new JSONObject();
                    data.put("counts", totalCartNum);
                    if (updateCount > 0) {
                        response.put("data", data);
                        response.put("code", "0000");
                        response.put("msg", "更新成功");
                    } else {
                        response.put("code", "0001");
                        response.put("msg", "更新失败");
                    }
                } else {//添加当前客户购物车商品
                    bsenDao.addProductC(order);
                    //查询当前客户购物车的所有商品数量
                    Integer totalCartNum = bsenDao.selectCartProductsCounts(paramsMap);
                    JSONObject data = new JSONObject();
                    data.put("counts", totalCartNum);
                    data.put("id", order.getId());
                    if (order.getId() > 0) {
                        response.put("data", data);
                        response.put("code", "0000");
                        response.put("msg", "添加成功");
                    } else {
                        response.put("code", "0001");
                        response.put("msg", "添加失败");
                    }
                }
            }
            //进行支付交易存储临时交易订单数据
            if (status == 2) {
                myRedisCache = getRedisTemplate();
                String tempOrderId = MD5.randomStr();
                myRedisCache.putObject(tempOrderId, order);
                JSONObject data = new JSONObject();
                data.put("tempOrderId", tempOrderId);
                response.put("data", data);
                response.put("code", "0000");
                response.put("msg", "添加成功,请在1小时内完成支付");
            }

        } else {
            response.put("code", "0001");
            response.put("msg", "该用户不存在");
        }
        return response;
    }

    /**
     * @author:黄孟军
     * @date:2019/1/2
     * @description:获取交易订单的商品信息
     */
    @Override
    public JSONObject getShoppingCartPro(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();

        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("userId", uId);
        }

        //获取http协议地址
        getHttpAddress();

        //获取订单详情
        if (!paramsMap.isEmpty()) {
            OrderInfo orderInfo = null;
            JSONArray resultOrder = new JSONArray();
            //判断订单是否正在交易: 2- 正在交易,0- 从购物车拿出
            String idsStr = (String) paramsMap.get("ids");
            logger.info("##ids:" + idsStr);
            List array = JSONArray.parseArray(idsStr);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                Map<String, Object> params = new HashMap<>();
                if (2 == (Integer) obj.get("status")) {
                    params.put("tempOrderId", obj.get("temporderid"));
                    Order order = getDealingBytempOrderId(params);
                    Map<String, Object> daoParams = new HashMap<>();
                    //产品pid
                    daoParams.put("pid", order.getPid());
                    orderInfo = bsenDao.selectDealingProduct(daoParams);
                    //对正在交易的产品封装
                    String priceStr = order.getPrice();
                    Integer priceInt = null;
                    if (priceStr != null) {
                        priceInt = Integer.valueOf(priceStr);
                    }
                    orderInfo.setPid(order.getPid());
                    orderInfo.setPrice(priceInt);
                    String turnOver = order.getTurnOver();
                    orderInfo.setTurnOver(turnOver);
                }
                if (0 == (Integer) obj.get("status")) {
                    params.put("userId", paramsMap.get("userId"));
                    params.put("id", obj.get("id"));
                    params.put("pid", obj.get("pid"));
                    params.put("status", obj.get("status"));
                    orderInfo = bsenDao.selectCartProducts(params);
                }
                if (orderInfo != null) {
                    String imgPath = httpStr + orderInfo.getImgUrl();
                    orderInfo.setImgUrl(imgPath);
                    resultOrder.add(orderInfo);
                    data.put("orderInfos", resultOrder);
                }
            }
            response.put("data", data);
            response.put("code", "0000");
            response.put("msg", "获取订单成功");
        }
        return response;
    }

    @Override
    public JSONObject getShoppingCartList(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("userId", uId);
            JSONObject data = new JSONObject();
            //获取http协议地址
            getHttpAddress();
            List<OrderInfo> productList = null;
            if (!paramsMap.isEmpty()) {
                productList = bsenDao.selectCartProductList(paramsMap);
                Iterator<OrderInfo> ite = productList.iterator();
                while (ite.hasNext()) {
                    OrderInfo entity = ite.next();
                    String imgPath = httpStr + entity.getImgUrl();
                    entity.setImgUrl(imgPath);
                }
                data.put("productList", productList);
            }
            if (productList != null) {
                response.put("data", data);
                response.put("code", "0000");
                response.put("msg", "查询成功");
            } else {
                response.put("code", "0001");
                response.put("msg", "查询失败");
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "该用户不存在");
        }
        return response;
    }

    @Override
    public JSONObject updateShoppingCartproNum(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("userId", uId);
            Integer one = bsenDao.updateProductNum(paramsMap);
            if (one == 1) {
                response.put("code", "0000");
                response.put("msg", "更新成功");
            } else {
                response.put("code", "0001");
                response.put("msg", "更新失败");
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "该用户不存在");
        }
        return response;
    }

    @Override
    public JSONObject myinfo(Map<String, Object> paramsMap) {
        String uId = getUId(paramsMap);
        JSONObject response = new JSONObject();
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("uId", uId);
            User user = bsenDao.selectUser(paramsMap);
            if (user != null) {
                JSONObject data = new JSONObject();
                data.put("userInfo", user);
                response.put("data", data);
                response.put("code", "0000");
                response.put("msg", "查询成功");
            } else {
                response.put("code", "0002");
                response.put("msg", "查询失败");
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "该用户不存在");
        }

        return response;
    }

    @Override
    public Map<String, Object> prepayment(HttpServletRequest request, Map<String, Object> paramsMap) {

        Map<String, Object> paramsWxMap = new HashMap<>();
        paramsWxMap.put("appid", "wx0cc8ab70b74f6211");//小程序ID
        paramsWxMap.put("mch_id", "1522104721");//商户号
        paramsWxMap.put("trade_type", "JSAPI");//交易类型

        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsWxMap.put("openid", uId);//用户标识
        }
        paramsWxMap.put("nonce_str", MD5.randomStr());//随机字符串
        String out_trade_no = Utils.generateOrderSN();
        paramsWxMap.put("out_trade_no", out_trade_no);//商户订单号
        paramsWxMap.put("body", "佰森门业" + "-" + out_trade_no);//商品描述

        String amountMoney = (String) paramsMap.get("amountMoney");
        paramsWxMap.put("total_fee", amountMoney);//标价金额

        String spbill_create_ip = Utils.getIpAddress(request);
        paramsWxMap.put("spbill_create_ip", spbill_create_ip);//终端IP

        paramsWxMap.put("notify_url", "http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php");//通知地址

        //对发送参数进行签名
        String stringA = Utils.formatUrlMap(paramsWxMap, false, false);
        stringA = stringA + "&key=2923965h6ua1ng1me6ngju81n4CCE050";

        String sign1 = null;
        try {
            sign1 = MD5.md5(stringA, "").toUpperCase();
        } catch (Exception e) {
            logger.info("发送参数签名异常", e);
        }
        paramsWxMap.put("sign", sign1);//签名

        logger.info("请求参数: " + paramsWxMap);
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        String xmlParamsStr = XmlUtil.encode(paramsWxMap);

        String result = HttpRequest.sendPost(url, xmlParamsStr);
        Document emlemet = null;
        try {
            emlemet = DocumentHelper.parseText(result);
        } catch (DocumentException e) {
            logger.info("xml转jsonObject异常:", e);
        }
        Map<String, Object> response = XmlUtil.elementToMap(emlemet.getRootElement());

        //对微信返回数据进行签名验证
        String wxSign = (String) response.get("sign");
        response.remove("sign");
        //按字典序对Map键值对排序
        String stringB = Utils.formatUrlMap(response, false, false);
        stringB = stringB + "&key=2923965h6ua1ng1me6ngju81n4CCE050";
        String sign2 = null;
        try {
            sign2 = MD5.md5(stringB, "").toUpperCase();
        } catch (Exception e) {
            logger.info("微信返回数据签名异常", e);
        }
        //对微信返回的数据进行签名验证
        if (wxSign.equals(sign2)) {
            if ("SUCCESS".equals(response.get("return_code"))) {
                if ("SUCCESS".equals(response.get("result_code"))) {
                    Map<String, Object> preResult = new HashMap<>();
                    preResult.put("appId", response.get("appid"));
                    String timeStamp = "" + System.currentTimeMillis();
                    preResult.put("timeStamp", timeStamp);//时间戳从1970年1月1日00:00:00至今的秒数,即当前的时间
                    String nonceStr = MD5.randomStr();
                    preResult.put("nonceStr", nonceStr);//随机字符串，长度为32个字符以下
                    preResult.put("signType", "MD5");//随机字符串，长度为32个字符以下
                    String _package = "prepay_id=" + response.get("prepay_id");
                    preResult.put("package", _package);//统一下单接口返回的 prepay_id 参数值，提交格式如：prepay_id=*
                    //按字典序对Map键值对排序
                    String _paysign = Utils.formatUrlMap(preResult, false, false);
                    _paysign = _paysign + "&key=2923965h6ua1ng1me6ngju81n4CCE050";
                    String paySign = null;
                    try {
                        paySign = MD5.md5(_paysign, "").toUpperCase();
                    } catch (Exception e) {
                        logger.info("微信最后拉起支付页面参数签名异常", e);
                    }
                    preResult.put("paySign", paySign);
                    preResult.put("out_trade_no", out_trade_no);
                    preResult.put("return_code", response.get("return_code"));
                    preResult.put("return_msg", response.get("return_msg"));
                    preResult.put("result_code", response.get("result_code"));
                    return preResult;
                } else {
                    return response;
                }
            } else {
                return response;
            }
        }
        return null;
    }

    @Override
    public JSONObject updateOrderInfo(Map<String, Object> paramsMap) {

        String uId = getUId(paramsMap);
        JSONObject response = new JSONObject();
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("userId", uId);
            String plistStr = (String) paramsMap.get("plist");
            logger.info("##plist:" + plistStr);
            String selfGet = (String) paramsMap.get("selfGet");
            JSONObject userObj = JSONObject.parseObject((String) paramsMap.get("userInfo"));
            List arrlist = JSONArray.parseArray(plistStr);
            for (int i = 0; i < arrlist.size(); i++) {
                logger.info("更新第" + (i + 1) + "个商品");
                Map<String, Object> params = new HashMap<>();
                JSONObject obj = (JSONObject) arrlist.get(i);
                params.put("userId", paramsMap.get("userId"));
                params.put("pid", obj.get("pid"));
                params.put("status", 1);
                String time = "" + System.currentTimeMillis();//支付完成保存订单时间
                params.put("time", time);
                params.put("turnOver", obj.get("orderNum"));
                params.put("price", obj.get("orderUnitPrice").toString());
                params.put("order_num", paramsMap.get("order_num"));

                params.put("self_get", selfGet);
                String name = (String) userObj.get("userName");
                String mobile = (String) userObj.get("telNumber");
                String provinceName = (String) userObj.get("provinceName");
                String cityName = (String) userObj.get("cityName");
                String countyName = (String) userObj.get("countyName");
                String detailInfo = (String) userObj.get("detailInfo");
                String address = "true".equals(selfGet) ? " " : provinceName + cityName + countyName + detailInfo;
                params.put("link_man", name);
                params.put("link_phone", mobile);
                params.put("link_address", address);
                params.put("order_status", "1");

                Integer count_ = bsenDao.selectProduct(params);
                if (count_ == 0) {
                    Integer count = bsenDao.addProductO(params);
                    if (count == 1) {
                        response.put("code", "0000");
                        response.put("msg", "添加成功");
                    } else {
                        response.put("code", "0001");
                        response.put("msg", "添加失败");
                    }
                } else {
                    Integer count = bsenDao.updateOrderInfo(params);
                    if (count == 1) {
                        response.put("code", "0000");
                        response.put("msg", "更新成功");
                    } else {
                        response.put("code", "0001");
                        response.put("msg", "更新失败");
                    }
                }
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "该用户不存在");
        }
        return response;
    }

    @Override
    public JSONObject myOrderList(Map<String, Object> paramsMap) {
        String uId = getUId(paramsMap);
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();

        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("userId", uId);

            //获取http协议地址
            getHttpAddress();
            List<Order> orderlist = null;
            if (!paramsMap.isEmpty()) {
                paramsMap.put("uId", uId);
                User user = bsenDao.selectUser(paramsMap);
                if (user.getAuthority() == 0 && !"o".equals(paramsMap.get("flag"))) {
                    paramsMap.put("uId", 0);
                    orderlist = bsenDao.selectAllUserOrderlist(paramsMap);
                } else {
                    orderlist = bsenDao.selectOrderlist(paramsMap);
                }
                Iterator<Order> ite = orderlist.iterator();
                while (ite.hasNext()) {
                    Order entity = ite.next();
                    String imgPath = httpStr + entity.getImgUrl();
                    entity.setImgUrl(imgPath);
                }
                data.put("orderlist", orderlist);
            }
            if (orderlist != null) {
                response.put("data", data);
                response.put("code", "0000");
                response.put("msg", "查询成功");
            } else {
                response.put("code", "0001");
                response.put("msg", "查询失败");
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "该用户不存在");
        }
        return response;
    }

    @Override
    public JSONObject myOrderInfo(Map<String, Object> paramsMap) {
        String uId = getUId(paramsMap);
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();

        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("userId", uId);

            //获取http协议地址
            getHttpAddress();
            OrderInfo orderInfo = null;
            if (!paramsMap.isEmpty()) {
                paramsMap.put("uId", uId);
                User user = bsenDao.selectUser(paramsMap);
                if (user.getAuthority() == 0 && "a".equals(paramsMap.get("flag"))) {
                    paramsMap.put("uId", 0);
                    orderInfo = bsenDao.adminSelectOrder(paramsMap);
                } else {
                    orderInfo = bsenDao.selectOrder(paramsMap);
                }
                if (!"$NON".equals(orderInfo.getTransportCode())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("transportCompany", orderInfo.getTransportCode());
                    Logistics logistics = bsenDao.selecttransportCompany(params);
                    String transportName = logistics.getCompanyName();
                    orderInfo.setTransportName(transportName);
                } else {
                    orderInfo.setTransportName("无");
                }

                String imgPath = httpStr + orderInfo.getImgUrl();
                orderInfo.setImgUrl(imgPath);
                data.put("orderInfo", orderInfo);
            }
            if (orderInfo != null) {
                response.put("data", data);
                response.put("code", "0000");
                response.put("msg", "查询成功");
            } else {
                response.put("code", "0001");
                response.put("msg", "查询失败");
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "该用户不存在");
        }
        return response;
    }

    @Override
    public JSONObject addOrderEvaluate(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("uId", uId);
            //检查用户是否重复评论
            Integer counts = bsenDao.selectUserOrderEvaluate(paramsMap);
            if (counts == 0) {
                String evaluateTime = "" + new Date().getTime();
                paramsMap.put("evaluate_time", evaluateTime);
                Integer oneComm = bsenDao.updateOrderEvaluate(paramsMap);
                JSONObject data = new JSONObject();
                if (oneComm == 1) {
//                    User user = bsenDao.selectUser(paramsMap);
//                    data.put("user", user);
//                    data.put("evaluateTime",evaluateTime);
                    data.put("evaluate", paramsMap.get("evaluate"));
                    response.put("data", data);
                    response.put("code", "0000");
                    response.put("msg", "更新成功");
                } else {
                    response.put("code", "0001");
                    response.put("msg", "更新失败");
                }
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "用户不存在");
        }
        return response;
    }

    @Override
    public JSONObject looksGoodFlow(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
        JSONObject data = null;
        try {
            String expCode = (String) paramsMap.get("transport_code");
            String expNo = (String) paramsMap.get("transport_num");
            String result = api.getOrderTracesByJson(expCode, expNo);
            data = JSONObject.parseObject(result);
            String transportCompany = (String) data.get("ShipperCode");
            Map<String, Object> params = new HashMap<>();
            params.put("transportCompany", transportCompany);
            Logistics logistics = bsenDao.selecttransportCompany(params);
            data.put("ShipperCode", logistics.getCompanyName());
            response.put("data", data);
            response.put("code", "0000");
            response.put("msg", "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", "0001");
            response.put("msg", "查询失败");
        }
        return response;
    }

    @Override
    public JSONObject searchFlowCompany(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        List<Logistics> logisticsList = bsenDao.selectCompanyLikeName(paramsMap);
        data.put("logisticsList", logisticsList);
        response.put("data", data);
        response.put("code", "0000");
        response.put("msg", "查询成功");
        return response;
    }

    @Override
    public JSONObject addOrderWuLiuInfo(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("uId", uId);
            User user = bsenDao.selectUser(paramsMap);
            if (user.getAuthority() == 0) {
                paramsMap.put("uId", 0);
                paramsMap.put("flow_time", System.currentTimeMillis());
                Integer count = bsenDao.addOrderWuLiuInfo(paramsMap);
                if (count > 0) {
                    response.put("code", "0000");
                    response.put("msg", "更新成功");
                } else {
                    response.put("code", "0001");
                    response.put("msg", "更新失败");
                }
            } else {
                response.put("code", "0001");
                response.put("msg", "无权限");
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "用户不存在");
        }
        return response;
    }

    @Override
    public JSONObject adddynamicdesc(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();

        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            String leaveAMessage = (String) paramsMap.get("desc");
            if (leaveAMessage != null && leaveAMessage != "") {
                Map<String, Object> sendDesc = new HashMap<>();
                sendDesc.put("userId", uId);
                sendDesc.put("desc", leaveAMessage);
                sendDesc.put("time", "" + new Date().getTime());
                //向数据库添加动态描述
                Integer oneDesc = bsenDao.addDynamicDesc(sendDesc);
                if (oneDesc == 1) {
                    response.put("code", "0000");
                    response.put("msg", "保存成功");
                } else {
                    response.put("code", "0001");
                    response.put("msg", "保存失败");
                }
            } else {
                response.put("code", "0001");
                response.put("msg", "保存失败");
            }
        } else {
            response.put("code", "0001");
            response.put("msg", "该用户不存在");
        }
        return response;
    }

    @Override
    public JSONObject getDynamics(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        //更新浏览量(传一个空的map对象占位
        boolean isTrue = (boolean) paramsMap.get("isTrue");
        if (isTrue) {
            bsenDao.updataDynamicsBrowseNum(paramsMap);
            String uId = getUId(paramsMap);
            if (uId != null) {
                paramsMap.remove("loginState");
                paramsMap.put("uId", uId);
                User user = bsenDao.selectUser(paramsMap);
                data.put("user", user);
            } else {
                response.put("code", "0001");
                response.put("msg", "该用户不存在");
                return response;
            }
        }

        //获取http协议地址
        getHttpAddress();
        //获取动态列表
        if (!paramsMap.isEmpty()) {
            List<Dynamic> dynamics = bsenDao.getDynamics(paramsMap);
            Iterator it = dynamics.iterator();
            while (it.hasNext()) {
                Dynamic entity = (Dynamic) it.next();
                List<String> contentImgs = entity.getContentImgs();
                Iterator<String> ite = contentImgs.iterator();
                List<String> imgs = new ArrayList<>();
                while (ite.hasNext()) {
                    String imgUrl = ite.next();
                    imgUrl = httpStr + imgUrl;
                    imgs.add(imgUrl);
                }
                entity.setContentImgs(imgs);
            }
            data.put("dynamics", dynamics);
            //封装返回数据
            response.put("data", data);
            response.put("code", "0000");
            response.put("msg", "success");
        }
        return response;
    }

    private String getUId(Map<String, Object> paramsMap) {
        String loginState = (String) paramsMap.get("loginState");
        myRedisCache = getRedisTemplate();
        JSONObject userObj = (JSONObject) myRedisCache.getObject(loginState);
        String uId = null;
        try {
            uId = (String) userObj.get("uId");
            logger.info("redis数据库获取openId值: "+uId);
        }catch (NullPointerException e){
            logger.error("redis数据库获取key值异常:",e);
        }
        return uId;
    }

    private Order getDealingBytempOrderId(Map<String, Object> paramsMap) {
        String tempOrderId = (String) paramsMap.get("tempOrderId");
        myRedisCache = getRedisTemplate();
        Order order = (Order) myRedisCache.getObject(tempOrderId);
        logger.info("redis数据库正在参与交易的order: " + order);
        return order;
    }

    private void getHttpAddress() {
        Map<String, Object> params = new HashMap<>();
        params.put("dicttypeid", "bsen");
        params.put("dictid", "localAddress");
        DictEntity dictEntity = bsenDao.getHttpAddress(params);
        String httpStr = dictEntity.getIp();
        setHttpStr(httpStr);
    }

    private JSONObject add(Map<String, Object> sendImgs) {
        JSONObject result = new JSONObject();
        if (sendImgs != null) {
            Integer oneImg = bsenDao.addDynamicImgs(sendImgs);
            if (oneImg == 1) {
                result.put("code", "0000");
                result.put("msg", "图片保存成功");
            } else {
                result.put("code", "0002");
                result.put("msg", "图片保存失败");
            }
        } else {
            result.put("code", "0001");
            result.put("msg", "图片上传失败");
        }
        return result;
    }
}
