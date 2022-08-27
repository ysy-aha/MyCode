package pers.yshy.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Http工具类
 *
 * @author yshy
 * @since 2022-08-16
 */
public class HttpsUtils {

    /**
     * HttpClient 文件上传
     *
     * @param url      请求地址
     * @param reqObj   请求参数
     * @param filePath 文件地址
     * @return 结果
     */
    public static String multiPost(String url, Object reqObj, String filePath) {
        // 边界
        String boundary = "----YBwEK8jZdhBT9SOHxGFbceGaSKA5HV0U";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            /**
             * 配置超时
             * setConnectTimeout：设置连接超时时间，单位毫秒。
             * setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
             * setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             */
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).build();
            httpPost.setConfig(config);

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setCharset(Charset.defaultCharset());
            entityBuilder.setBoundary(boundary);
            // 添加请求参数
            StringBody reqStr = new StringBody(JSON.toJSONString(reqObj), ContentType.create("multipart/form-data", Charset.defaultCharset()));
            entityBuilder.addPart("req_msg", reqStr);
            FileBody reqFile = new FileBody(new File(filePath));
            entityBuilder.addPart("req_file", reqFile);

            HttpEntity reqEntity = entityBuilder.build();
            httpPost.setEntity(reqEntity);
            // 执行post请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // 获得相应实体
                HttpEntity rspEntity = response.getEntity();
                if (rspEntity != null) {
                    return EntityUtils.toString(rspEntity, StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
