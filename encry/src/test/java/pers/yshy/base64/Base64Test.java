package pers.yshy.base64;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 测试各类的Base64
 *
 * @author ysy
 * @since 2022/9/20
 */
public class Base64Test {

    private final String utf8 = StandardCharsets.UTF_8.name();

    /**
     * 使用JDK原生的Base64，从1.8开始支持
     * java.util.Base64
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void test1() throws UnsupportedEncodingException {
        String testStr = "张三007";
        byte[] testBytes = testStr.getBytes(utf8);
        // 转码
        String encStr = Base64.getEncoder().encodeToString(testBytes);
        // 输出：5byg5LiJMDA3
        System.out.println("转码后：" + encStr);
        // 解码
        byte[] decBytes = Base64.getDecoder().decode(encStr.getBytes(utf8));
        // 输出：张三007
        System.out.println("解码后：" + new String(decBytes));

        testStr = "张00";
        testBytes = testStr.getBytes(utf8);
        // 转码
        encStr = Base64.getEncoder().encodeToString(testBytes);
        // 输出：5bygMDA=
        System.out.println("转码后：" + encStr);
        // 解码
        decBytes = Base64.getDecoder().decode(encStr.getBytes(utf8));
        // 输出：张00
        System.out.println("解码后：" + new String(decBytes));
    }

    @Test
    public void test2() throws UnsupportedEncodingException {
        String testStr = "张三007";
        byte[] testBytes = testStr.getBytes(utf8);
        // 转码
        String encStr = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(testBytes);
        // 输出：5byg5LiJMDA3
        System.out.println("转码后：" + encStr);
        // 解码
        byte[] decBytes = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(encStr);
        // 输出：张三007
        System.out.println("解码后：" + new String(decBytes));
    }

    @Test
    public void test3() throws UnsupportedEncodingException {
        String testStr = "张三007";
        byte[] testBytes = testStr.getBytes(utf8);
        // 转码
        String encStr = org.apache.commons.codec.binary.Base64.encodeBase64String(testBytes);
        // 输出：5byg5LiJMDA3
        System.out.println("转码后：" + encStr);
        // 解码
        byte[] decBytes = org.apache.commons.codec.binary.Base64.decodeBase64(encStr);
        // 输出：张三007
        System.out.println("解码后：" + new String(decBytes));
    }
}
