package com.nbcb.myron.bsen.utils;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

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
}
