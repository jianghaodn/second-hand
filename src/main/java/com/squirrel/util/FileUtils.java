package com.squirrel.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;


/**
 * @author Administrator
 */
@Component
@ConfigurationProperties(prefix = "oss")
@Data
@Slf4j
public class FileUtils {

    String accessKey;
    String secretKey;
    String bucket;
    String url;

    /**
     * 将文件保存到七牛云oss
     *
     * @param file 文件上传对象
     * @return 文件的远程url
     */
    public String saveFile(MultipartFile file) {
        InputStream inputStream = null;
        String fileName = "";
        try {
            inputStream = file.getInputStream();
            fileName = file.getOriginalFilename();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return updateFile(inputStream, fileName);
    }

    /**
     * 上传文件接口
     *
     * @param file 前端传来的文件对象
     * @return 文件的url
     */
    public String updateFile(MultipartFile file) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            return updateFile(inputStream, file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 上传一个文件
     *
     * @param inputStream 文件输入流，从MultipartFile获取
     * @return 文件的路径
     */
    public String updateFile(InputStream inputStream, String fileName) {
        if (Objects.isNull(inputStream)) {
            return "";
        }
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        // 指定分片上传版本
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;

        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = getKey(fileName);
        String url = "http://rjqkeiur2.hn-bkt.clouddn.com/" + key;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = null;
            try {
                response = uploadManager.put(inputStream, key, upToken, null, null);
            } catch (QiniuException e) {
                e.printStackTrace();
            }
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
            return url;
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }

        }
        return "";
    }

    /**
     * 创建一个文件路径
     *
     * @param fileName
     * @return
     */
    private String getKey(String fileName) {
        String prefix;
        String type = "";
        if (!StringUtils.hasText(fileName)) {
            prefix = "null/";
        } else {
            prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
            type = "." + prefix;
        }
        String mid = LocalDate.now().toString().replace("-", "/") +"/";
        return prefix+"/" + mid + UUID.randomUUID().toString().replace("-", "") + type;
    }
}
