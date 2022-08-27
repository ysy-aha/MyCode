package pers.yshy.test;

import pers.yshy.utils.HttpsUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传测试
 *
 * @author ysy
 * @since 2022-08-16
 */
public class UploadTest {
    public static void main(String[] args) {
        String filePath = "src/main/resources/test.json";
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("userName", "张三");
        reqMap.put("age", "17");
        String rspStr = HttpsUtils.multiPost("http://127.0.0.1:999/file/upload", reqMap, filePath);
        System.out.println(rspStr);
    }
}
