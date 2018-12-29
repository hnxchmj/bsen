package com.nbcb.myron.bsen.utils;

import com.alibaba.fastjson.JSONObject;
import com.nbcb.myron.bsen.common.MD5;
import com.nbcb.myron.bsen.module.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private static String wxSessionkey = "F3UENUg3JcI31O2RpoBQ9n8J77Tf1LgZUyGyzdjm7Q4rRKT052DPLdA3NqHeajF6cITOX54rQ2yoFxE83g3eHWjEH7CB9m2FvdoljuTXZLrJy6U2Ba2EbUlF6xazawRaK9Aq";

    public static Map<String,Object> getMap(JSONObject jsonObj){
        Map<String,Object> sendMap = new HashMap<>();
        if (jsonObj.get("code")=="0000"){
            JSONObject data = (JSONObject)jsonObj.get("data");
            sendMap.put("uId","oM78247bYUgo2bsq8ZUEAf_d-GLM");
            sendMap.put("dId",data.get("did"));
            sendMap.put("pId"," ");
            sendMap.put("path",data.get("path"));
            sendMap.put("fileName",data.get("filename"));
            sendMap.put("classify","3");
            return sendMap;
        }
        return null;
    }
    public static JSONObject saveFile(MultipartFile file){
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
        String month=new SimpleDateFormat("yyyyMM").format(date);
        String day=new SimpleDateFormat("yyyyMMdd").format(date);
        //文件存放路径
        String imageFilePath =File.separator+"home"+File.separator+"dynamicimgs"+ File.separator+month+File.separator+day+ File.separator;

        String imageName=file.getOriginalFilename();//生成文件名

        String fullPath = "/home/hmj/java/bsen"+imageFilePath;//D:\MultipartFile
        //创建文件夹
        File f=new File(fullPath);
        if(!f.exists()){
            f.mkdirs();
        }
        //图片的完整路径
        String path=fullPath+imageName;
        logger.info("上传图片路径:"+path);
        JSONObject map = new JSONObject();

        try {
            file.transferTo(new File(path));
        }catch (IOException io){
            logger.error("",io);
            map.put("msg","上传图片失败");
            map.put("code","0001");
            return map;
        }

        JSONObject data = new JSONObject();
        data.put("path",imageFilePath);//保存路径
        data.put("filename",imageName);//保存文件名

        map.put("msg","上传图片成功");
        map.put("code","0000");
        map.put("data",data);

        return map;
    }


    /*
    *微信用户校验
     */
    public static void putSession(User u) {
//        BeanManager.getSpyMemcachedClient().set(wxSessionkey,  3*24*3600,u.getOpenid()+","+u.getSession_key());//设置memcache缓存
    }


    /**
     * 得到3rd_session登录效验(key)
     * @return
     */
    public static String get3rdSession(){
        return exec("head -n 80 /dev/urandom | tr -dc A-Za-z0-9 | head -c 168");
    }


    /**
     * linux中执行命令
     * @param cmd
     * @return
     */
    private static String exec(String cmd) {
        StringBuffer sb = new StringBuffer();
        try {
            String[] cmdA = { "/bin/sh", "-c", cmd };
            Process process = Runtime.getRuntime().exec(cmdA);
            LineNumberReader br = new LineNumberReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            //如果本地测试，会报空指针异常，所以为了不让报错，索性返回有值即可
            sb.append(wxSessionkey);
        }
        return sb.toString();
    }

    /**
     * 针对微信支付生成商户订单号，为了避免微信商户订单号重复（下单单位支付），
     * @return
     */
    public static String generateOrderSN() {
        StringBuffer orderSNBuffer = new StringBuffer();
        orderSNBuffer.append(System.currentTimeMillis());
        orderSNBuffer.append(getRandomString(7));
        return orderSNBuffer.toString();
    }
    /**
     * 获取随机字符串
     * @param len
     * @return
     */
    public static String getRandomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        Random rnd  = new Random();
        String AB = MD5.randomStr();
        for (int i = 0; i < len; i++){
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    /**
     * @Title: getIpAddress
     * @Description: 获取客户端真实IP地址
     * @author yihj
     * @param @param request
     * @param @param response
     * @param @return    参数
     * @return String    返回类型
     * @throws
     */
    public static String getIpAddress(HttpServletRequest request) {
        // 避免反向代理不能获取真实地址, 取X-Forwarded-For中第一个非unknown的有效IP字符串
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    /**
     *
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     *
     * @param paraMap   要排序的Map对象
     * @param urlEncode   是否需要URLENCODE
     * @param keyToLower    是否需要将Key转换为全小写
     *            true:key转化成小写，false:不转化
     * @return
     */
    public static String formatUrlMap(Map<String, Object> paraMap, boolean urlEncode, boolean keyToLower){
        String buff = "";
        Map<String, Object> tmpMap = paraMap;
        try
        {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds)
            {
                if (StringUtils.isNotBlank(item.getKey()))
                {
                    String key = item.getKey();
                    String val = (String)item.getValue();
                    if (urlEncode)
                    {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower)
                    {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else
                    {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false)
            {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e)
        {
            return null;
        }
        return buff;
    }
}