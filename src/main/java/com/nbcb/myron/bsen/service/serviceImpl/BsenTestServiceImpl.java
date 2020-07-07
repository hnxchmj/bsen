package com.nbcb.myron.bsen.service.serviceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nbcb.myron.bsen.common.HttpRequest;
import com.nbcb.myron.bsen.common.MD5;
import com.nbcb.myron.bsen.common.MyRedisCache;
import com.nbcb.myron.bsen.dao.BsenTestDao;
import com.nbcb.myron.bsen.module.*;
import com.nbcb.myron.bsen.service.BsenTestService;
import com.nbcb.myron.bsen.utils.KdniaoTrackQueryAPI;
import com.nbcb.myron.bsen.utils.Utils;
import com.nbcb.myron.bsen.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Service
public class BsenTestServiceImpl implements BsenTestService {


    private MyRedisCache myRedisCache;
    private String httpStr;

    @Autowired
    private BsenTestDao bsenTestDao;

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

        String nickName = (String) paramsMap.get("nickName");
        String avatarUrl = (String) paramsMap.get("avatarUrl");
        Integer gender = (Integer) paramsMap.get("gender");

        JSONObject data = getWxData(paramsMap);

        log.info("微信返回数据data" + data);
        JSONObject response = new JSONObject();

        if (data.get("openid") != null) {
            //对用户session_key 进行加密处理
            String session_key = (String) data.get("session_key");//密钥key
            String openid = (String) data.get("openid");
            String loginState = null;
            try {
                loginState = MD5.md5(openid, session_key);//维护后的登录态
            } catch (Exception e) {
                log.info("9999", e);
            }
            JSONObject acceptParams = new JSONObject();
            acceptParams.put("uId", data.get("openid"));
            //查询该用户是否存在
            User user = bsenTestDao.selectUser(acceptParams);

            acceptParams.put("gender", gender);
            acceptParams.put("avatarUrl", avatarUrl);
            acceptParams.put("nickName", nickName);
            acceptParams.put("session_key", session_key);
            acceptParams.put("login_state", loginState);
            if (user == null) {//添加新用户
                Integer isNum = bsenTestDao.insertNewUser(acceptParams);
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
                Integer isNum = bsenTestDao.updateUserLoginStatus(acceptParams);
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
            log.info("###存入redis用户参数" + acceptParams.toJSONString());
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
        List<ImageEntity> iEntityList = bsenTestDao.getImageEntityList();
        Iterator it = iEntityList.iterator();
        while (it.hasNext()) {
            ImageEntity entity = (ImageEntity) it.next();
            String imgPath = httpStr + entity.getImgPath();
            entity.setImgPath(imgPath);
        }
        data.put("headimgswiper", iEntityList);
        //获取砍价商品信息
        List<CutProduct> cutBestHotProducts = bsenTestDao.getBestCutProducts();
        Iterator ite = cutBestHotProducts.iterator();
        while (ite.hasNext()) {
            CutProduct entity = (CutProduct) ite.next();
            String imgPath = httpStr + entity.getImgUrl();
            entity.setImgUrl(imgPath);
        }
        data.put("cutProducts", cutBestHotProducts);
        //获取精品推荐数据
        List<BoutiqueProduct> boutiqueProducts = bsenTestDao.getBoutiqueProducts();
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
            prcItems = bsenTestDao.getProductLists(paramsMap);
        } else {
            String conf = (String) paramsMap.get("cond");
            if ("zh".equals(conf)) {

            } else if ("zr".equals(conf)) {

            } else if ("xp".equals(conf)) {

            } else {//价格
                if (conf.indexOf("true") > 0) {//升
                    prcItems = bsenTestDao.getProductListsJgS(paramsMap);
                } else {//降
                    prcItems = bsenTestDao.getProductListsJgJ(paramsMap);
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
            String soldNum = bsenTestDao.selectProductSoldNum(params);
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
        } else {
            response.put("code", "0001");
            response.put("msg", "该用户不存在");
            return response;
        }
        boolean isUpdateDz = (boolean) paramsMap.get("isUpdateDz");
        if (isUpdateDz) {//更新详情页点赞
            Integer isNum = null;
            UserProduct userProduct = bsenTestDao.selectDz(paramsMap);
            if (userProduct != null) {
                if (userProduct.getIsdz() == 1) {
                    isNum = bsenTestDao.updateDzM(paramsMap);//取消赞
                } else {
                    isNum = bsenTestDao.updateDzP(paramsMap);//添加赞
                }
            } else {
                isNum = bsenTestDao.insertDz(paramsMap);//添加点赞
            }
            if (isNum == 1) {
                //查询对当前商品点赞的所有人员头像
                paramsMap.remove("userId");
                List<User> usersList = bsenTestDao.selectParsePsersons(paramsMap);
                data.put("praiseMans", usersList);
            }
            response.put("data", data);
            response.put("code", "0000");
            response.put("msg", "更新赞成功");
        } else {//查询详情信息
            //刷新浏览记录
            bsenTestDao.updateProductBrowseNum(paramsMap);
            //获取http协议地址
            getHttpAddress();
            //获取详情页数据
            Product product = bsenTestDao.getDetail(paramsMap);
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
            Integer totalCartNum = bsenTestDao.selectCartProductsCounts(paramsMap);
            data.put("counts", totalCartNum);
            //查询对当前商品点赞的所有人员头像
            paramsMap.remove("userId");
            List<User> usersList = bsenTestDao.selectParsePsersons(paramsMap);
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
        List<CutProduct> cutProducts = bsenTestDao.getCutProductsList();
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
        List<ProductListEntity> prcItems = bsenTestDao.getSearchProducts(params);
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
        Integer dynamicNum = bsenTestDao.selectDynamicNum();
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
        bsenTestDao.updataDynamicsBrowseNum(paramsMap);
        //获取http协议地址
        getHttpAddress();
        //获取动态详情
        if (!paramsMap.isEmpty()) {
            Dynamic dynamicdetail = bsenTestDao.dynamicDetail(paramsMap);
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
            log.info("##赞  +1");
            isNum = bsenTestDao.updatePlus(paramsMap);
        } else {
            log.info("##赞  -1");
            isNum = bsenTestDao.updateMinus(paramsMap);
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
        Integer counts = bsenTestDao.selectUserComment(paramsMap);
        if (counts == 0) {
            Integer oneComm = bsenTestDao.insertComment(paramsMap);
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
                Integer count = bsenTestDao.selectProduct(order);
                if (count > 0) {//更新当前客户所添加的产品数量
                    Integer updateCount = bsenTestDao.updateProduct(order);
                    //查询当前客户购物车的所有商品数量
                    Integer totalCartNum = bsenTestDao.selectCartProductsCounts(paramsMap);
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
                    bsenTestDao.addProductC(order);
                    //查询当前客户购物车的所有商品数量
                    Integer totalCartNum = bsenTestDao.selectCartProductsCounts(paramsMap);
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
            log.info("##ids:" + idsStr);
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
                    orderInfo = bsenTestDao.selectDealingProduct(daoParams);
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
                    orderInfo = bsenTestDao.selectCartProducts(params);
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
                productList = bsenTestDao.selectCartProductList(paramsMap);
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
            Integer one = bsenTestDao.updateProductNum(paramsMap);
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
    public JSONObject myInfo(Map<String, Object> paramsMap) {
        String uId = getUId(paramsMap);
        JSONObject response = new JSONObject();
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("uId", uId);
            User user = bsenTestDao.selectUser(paramsMap);
            if (user != null) {
                //如果是管理员查询是否有待处理事件(待处理订单或者待回复消息)
                if (user.getAuthority() == 0) {
                    //查询需要回复的消息数
                    Integer msgNum = bsenTestDao.selectToDoReplyMsgCount();
                    log.info("待回复消息数msgNum: " + msgNum);
                    //查询需要发货的订单数
                    Integer orderNum = bsenTestDao.selectToDoOrderCount();
                    log.info("待发货订单数orderNum: " + orderNum);
                    if (msgNum > 0) {
                        user.setToDoMsgFlag("true");
                    } else {
                        user.setToDoMsgFlag("false");
                    }
                    if (orderNum > 0) {
                        user.setToDoOrderFlag("true");
                    } else {
                        user.setToDoOrderFlag("false");
                    }
                } else {
                    user.setToDoMsgFlag("false");
                    user.setToDoOrderFlag("false");
                }
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
        amountMoney = Double.valueOf(amountMoney).intValue()*100+"";
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
            log.info("发送参数签名异常", e);
        }
        paramsWxMap.put("sign", sign1);//签名

        log.info("请求参数: " + paramsWxMap);
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        String xmlParamsStr = XmlUtil.encode(paramsWxMap);

        String result = HttpRequest.sendPost(url, xmlParamsStr);
        log.info("返回result: " + result);
        Document emlemet = null;
        try {
            emlemet = DocumentHelper.parseText(result);
        } catch (DocumentException e) {
            log.info("xml转jsonObject异常:", e);
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
            log.info("微信返回数据签名异常", e);
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
                        log.info("微信最后拉起支付页面参数签名异常", e);
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
            log.info("##plist:" + plistStr);
            String selfGet = (String) paramsMap.get("selfGet");
            JSONObject userObj = JSONObject.parseObject((String) paramsMap.get("userInfo"));
            List arrlist = JSONArray.parseArray(plistStr);
            for (int i = 0; i < arrlist.size(); i++) {
                log.info("更新第" + (i + 1) + "个商品");
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

                Integer count_ = bsenTestDao.selectProduct(params);
                if (count_ == 0) {
                    Integer count = bsenTestDao.addProductO(params);
                    if (count == 1) {
                        response.put("code", "0000");
                        response.put("msg", "添加成功");
                    } else {
                        response.put("code", "0001");
                        response.put("msg", "添加失败");
                    }
                } else {
                    Integer count = bsenTestDao.updateOrderInfo(params);
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
                User user = bsenTestDao.selectUser(paramsMap);
                if (user.getAuthority() == 0 && !"o".equals(paramsMap.get("flag"))) {
                    paramsMap.put("uId", 0);
                    orderlist = bsenTestDao.selectAllUserOrderlist(paramsMap);
                } else {
                    orderlist = bsenTestDao.selectOrderlist(paramsMap);
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
                User user = bsenTestDao.selectUser(paramsMap);
                if (user.getAuthority() == 0 && "a".equals(paramsMap.get("flag"))) {
                    paramsMap.put("uId", 0);
                    orderInfo = bsenTestDao.adminSelectOrder(paramsMap);
                } else {
                    orderInfo = bsenTestDao.selectOrder(paramsMap);
                }
                if (!"$NON".equals(orderInfo.getTransportCode())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("transportCompany", orderInfo.getTransportCode());
                    Logistics logistics = bsenTestDao.selecttransportCompany(params);
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
            Integer counts = bsenTestDao.selectUserOrderEvaluate(paramsMap);
            if (counts == 0) {
                String evaluateTime = "" + new Date().getTime();
                paramsMap.put("evaluate_time", evaluateTime);
                Integer oneComm = bsenTestDao.updateOrderEvaluate(paramsMap);
                JSONObject data = new JSONObject();
                if (oneComm == 1) {
//                    User user = bsenTestDao.selectUser(paramsMap);
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
            Logistics logistics = bsenTestDao.selecttransportCompany(params);
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
        List<Logistics> logisticsList = bsenTestDao.selectCompanyLikeName(paramsMap);
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
            User user = bsenTestDao.selectUser(paramsMap);
            if (user.getAuthority() == 0) {
                paramsMap.put("uId", 0);
                paramsMap.put("flow_time", System.currentTimeMillis());
                Integer count = bsenTestDao.addOrderWuLiuInfo(paramsMap);
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
    public boolean addUserMessage(Map<String, Object> paramsMap) {
        boolean isSuccess = false;
        if (paramsMap.get("MsgId") != null) {
            //更新用户会话状态:0-没有;1-有.
            bsenTestDao.updateUserIsSession(paramsMap);
            //查询消息是否存在
            Integer isNum = bsenTestDao.selectMessageByMsgId(paramsMap);
            if (isNum == 0) {
                //不存在时,插入新消息
                String FromUserNameToAdmin = paramsMap.get("FromUserName") + "TO" + "oFmnm5QU1lUnh_RgmwzUUwUopPqA";//用户TO客服发消息
                paramsMap.put("FromUserName", FromUserNameToAdmin);
                String isReaded = "1";//消息状态:0-已读;1-未读
                paramsMap.put("isReaded", isReaded);
                Integer count = bsenTestDao.addMessage(paramsMap);
                if (count > 0) {
                    //存入缓存48小时,消息处于可回复时间
                    myRedisCache = getRedisTemplate();
                    String FromUserName = (String) paramsMap.get("FromUserName");
                    String MsgId = paramsMap.get("MsgId") + "";
                    myRedisCache.putUserReplyChatExpiryTime(FromUserName, MsgId);//每个人的openid对应的消息id, 48小时失效
                    isSuccess = true;
                }
            }
        }
        return isSuccess;
    }

    @Override
    public JSONObject chatList(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("uId", uId);
            User user = bsenTestDao.selectUser(paramsMap);
            if (user.getAuthority() == 0) {
                paramsMap.remove("uId");
                List<UserMessage> usersMessagesList = bsenTestDao.selectMessageList(paramsMap);
                JSONObject data = new JSONObject();
                data.put("usersMessagesList", usersMessagesList);
                response.put("data", data);
                response.put("code", "0000");
                response.put("msg", "查询成功");
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
    public JSONObject chatDetail(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        String uId = getUId(paramsMap);
        if (uId != null) {
            paramsMap.remove("loginState");
            paramsMap.put("uId", uId);
            User user = bsenTestDao.selectUser(paramsMap);
            if (user.getAuthority() == 0) {
                String userToAdmin = paramsMap.get("userUId") + "TO" + uId;
                String adminToUser = uId + "TO" + paramsMap.get("userUId");
                paramsMap.remove("uId");
                paramsMap.put("userToAdmin", userToAdmin);
                paramsMap.put("adminToUser", adminToUser);
                List<Message> usersMessagesDetailList = bsenTestDao.getUserMessage(paramsMap);
                JSONObject data = new JSONObject();
                Iterator it = usersMessagesDetailList.iterator();
                List<MessageDetail> messageLists = new ArrayList<>();
                while (it.hasNext()) {
                    Message entity = (Message) it.next();
                    String sessionID = entity.getSessionID();
                    String openID = sessionID.substring(0, sessionID.indexOf("TO"));
                    Map<String, Object> param = new HashMap<>();
                    param.put("uId", openID);
                    User messageUserInfo = bsenTestDao.selectUser(param);
                    MessageDetail messageDetail = new MessageDetail();
                    messageDetail.setId(entity.getId());
                    messageDetail.setMsgId(entity.getMsgId());
                    messageDetail.setuId(openID);
                    messageDetail.setNickName(messageUserInfo.getNickName());
                    messageDetail.setHeadImgPath(messageUserInfo.getAvatarUrl());
                    messageDetail.setContent(entity.getContent());
                    messageDetail.setCreateTime(entity.getCreatedTime());
                    messageDetail.setType(entity.getType());
                    messageDetail.setIsReaded(entity.getIsReaded());
                    messageLists.add(messageDetail);
                }
                data.put("usersMessagesDetailList", messageLists);
                data.put("selfId", uId);
                response.put("data", data);
                response.put("code", "0000");
                response.put("msg", "查询成功");
                //更新聊天信息状态为已读
                bsenTestDao.updateMsgStatus(paramsMap);
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
    public JSONObject replyUserMsg(Map<String, Object> paramsMap) {
        JSONObject response = new JSONObject();
        myRedisCache = getRedisTemplate();
        String redisAccessToken = (String) myRedisCache.getObject("access_token");
        if (redisAccessToken == null) {
            //getAccessToken
            JSONObject data = getAccessToken();
            Integer errcode = (Integer) data.get("errcode");
            if (errcode == null || errcode == 0) {
                String access_token = (String) data.get("access_token");
                Integer expires_in = (Integer) data.get("expires_in");
                myRedisCache.putObjectTime("access_token", access_token, expires_in);
                redisAccessToken = (String) myRedisCache.getObject("access_token");
            } else {
                String errmsg = (String) data.get("errmsg");
                response.put("code", "0001");
                response.put("msg", errmsg);
                return response;
            }
        }
        Map<String, Object> sendMap = new HashMap<>();
        String touser = "\'" + paramsMap.get("toUser") + "\'";
        sendMap.put("\'touser\'", touser);
        //文本消息
        if ("text".equals(paramsMap.get("type"))) {
            String type = "\'" + paramsMap.get("type") + "\'";
            sendMap.put("\'msgtype\'", type);
            String content = "{\'content\':\'" + paramsMap.get("content") + "\'}";
            sendMap.put("\'text\'", content);
        }
        JSONObject wxResponse = sendCustomerMessage(redisAccessToken, sendMap);
        if (0 == (Integer) wxResponse.get("errcode")) {
            //插入客服TO用户新消息
            sendMap.clear();
            //(#{ToUserName},#{FromUserName},#{Content},#{MsgType},#{CreateTime},#{MsgId});

            sendMap.put("ToUserName", "gh_5230abfed2b9");//小程序原始ID

            String adminToUserName = paramsMap.get("adminId") + "TO" + paramsMap.get("toUser");//客服TO用户发消息
            sendMap.put("FromUserName", adminToUserName);

            String content = (String) paramsMap.get("content");//消息内容
            sendMap.put("Content", content);

            String type = (String) paramsMap.get("type");//消息类型
            sendMap.put("MsgType", type);

            String date = paramsMap.get("date") + "";//创建消息时间
            sendMap.put("CreateTime", date);

            String msgId = MD5.random64NumStr();//消息ID
            sendMap.put("MsgId", msgId);

            String isReaded = "0";//消息状态:0-已读;1-未读
            sendMap.put("isReaded", isReaded);

            Integer count = bsenTestDao.addMessage(sendMap);
            if (count > 0) {
                response.put("code", "0000");
                response.put("msg", "success");
            } else {
                response.put("code", "0001");
                response.put("msg", "fail");
            }
        } else {
            response.put("code", "0001");
            response.put("msg", wxResponse.get("errmsg"));
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
                Integer oneDesc = bsenTestDao.addDynamicDesc(sendDesc);
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
            String uId = getUId(paramsMap);
            if (uId != null) {
                bsenTestDao.updataDynamicsBrowseNum(paramsMap);
                paramsMap.remove("loginState");
                paramsMap.put("uId", uId);
                User user = bsenTestDao.selectUser(paramsMap);
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
            List<Dynamic> dynamics = bsenTestDao.getDynamics(paramsMap);
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
            log.info("redis数据库获取openId值: " + uId);
        } catch (NullPointerException e) {
            log.error("redis数据库获取key值异常:", e);
        }
        return uId;
    }

    private Order getDealingBytempOrderId(Map<String, Object> paramsMap) {
        String tempOrderId = (String) paramsMap.get("tempOrderId");
        myRedisCache = getRedisTemplate();
        Order order = (Order) myRedisCache.getObject(tempOrderId);
        log.info("redis数据库正在参与交易的order: " + order);
        return order;
    }

    private void getHttpAddress() {
        Map<String, Object> params = new HashMap<>();
        params.put("dicttypeid", "bsen");
        params.put("dictid", "localAddress");
        DictEntity dictEntity = bsenTestDao.getHttpAddress(params);
        String httpStr = dictEntity.getIp();
        setHttpStr(httpStr);
    }

    private JSONObject add(Map<String, Object> sendImgs) {
        JSONObject result = new JSONObject();
        if (sendImgs != null) {
            Integer oneImg = bsenTestDao.addDynamicImgs(sendImgs);
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

    private JSONObject getWxData(Map<String, Object> paramsMap) {
        //向微信接口校验
        String code = (String) paramsMap.get("code");
        String appid = "wx0cc8ab70b74f6211";
        String secret = "be78809d3f2f4447ead9735a574cba85";
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        String params = "appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        String result = HttpRequest.sendGet(url, params);
        JSONObject data = JSONObject.parseObject(result);
        return data;
    }

    /**
     * @date:2019/1/23
     * @time:20:04
     * @description:获取accessToken
     */
    private JSONObject getAccessToken() {
        String appid = "wx0cc8ab70b74f6211";
        String secret = "be78809d3f2f4447ead9735a574cba85";
        String url = "https://api.weixin.qq.com/cgi-bin/token";
        String params = "grant_type=client_credential&appid=" + appid + "&secret=" + secret;
        String result = HttpRequest.sendGet(url, params);
        JSONObject data = JSONObject.parseObject(result);
        log.info("redis缓存失效后重新获取小程序全局唯一后台接口调用凭据: " + data);
        return data;
    }

    /**
     * @date:2019/1/23
     * @time:20:04
     * @description:发送客服消息给用户
     */
    private JSONObject sendCustomerMessage(String accessToken, Map<String, Object> jsonMap) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken;
        JSONObject params = JSONObject.parseObject(jsonMap.toString().replace("=", ":"));
        String result = HttpRequest.sendPost(url, params.toJSONString());
        JSONObject data = JSONObject.parseObject(result);
        return data;
    }

    public static void main(String[] args) {
        BsenTestServiceImpl a = new BsenTestServiceImpl();
        Map<String, Object> map = new HashMap<>();
        map.put("\'touser\'", "\'oFmnm5S2acq0y7METZzQKJwXSltc\'");
        map.put("\'msgtype\'", "\'text\'");
        map.put("\'text\'", "{\'content\':\'发这些干什么?\'}");
//        System.out.print(a.getAccessToken());
        String token = "18_QD8JRwpYAEKDpld4TriwhhXqnqG7adjdhhc23U3NYRkyHJsdKsrayzDlGYkVvitQ6I8TX4Vk4eJEOAZt0LNy3yQpxJ8FAVQJbmeIPNAKVkDkXT_mOlMnvo3HoxwRSIdAEAMND";
        JSONObject response = a.sendCustomerMessage(token, map);
        System.out.print(response);
    }
}