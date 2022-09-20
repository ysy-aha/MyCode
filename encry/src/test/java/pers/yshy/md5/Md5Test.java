package pers.yshy.md5;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author ysy
 * @since 2022/9/20
 */
public class Md5Test {

    private final String utf8 = StandardCharsets.UTF_8.name();

    @Test
    public void test() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String testStr = "张三";
        // 生成数据摘要对象
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        // 通过md5算法计算
        byte[] encBytes = md5.digest(testStr.getBytes(utf8));

        // [97, 93, -75, 122, -93, 20, 82, -102, -86, 15, -66, -107, -77, -23, 91, -45]
        System.out.println(Arrays.toString(encBytes));

        // 如果直接输出字符串：a]�z�R������[�，会出现乱码，因为Ascll编码表中不存在负数
        System.out.println(new String(encBytes));

        // 将加密数据转换成16进制的字符串
        // 方法一
        BigInteger bigInteger = new BigInteger(1, encBytes);
        // 615db57aa314529aaa0fbe95b3e95bd3
        System.out.println("方法一获取加密结果：" + bigInteger.toString(16));

        // 方法二:
        StringBuilder stringBuilder = new StringBuilder();
        for (byte encByte : encBytes) {
            // 获取当前变量的补码的后八位
            String s = Integer.toHexString((int)encByte & 0xff);
            if (s.length() == 1) {
                stringBuilder.append(0);
            }
            stringBuilder.append(s);
        }
        System.out.println("方法二获取加密结果：" + stringBuilder);
    }

    @Test
    public void test1() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String testStr = "张三";
        // 生成数据摘要对象
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        // 通过md5算法计算，这里如果数据量较大，可使用循环添加update的方式
        md5.update(testStr.getBytes(utf8));
        byte[] encBytes = md5.digest();

        // [97, 93, -75, 122, -93, 20, 82, -102, -86, 15, -66, -107, -77, -23, 91, -45]
        System.out.println(Arrays.toString(encBytes));

        // 将加密数据转换成16进制的字符串
        BigInteger bigInteger = new BigInteger(1, encBytes);
        // 615db57aa314529aaa0fbe95b3e95bd3
        System.out.println("加密结果：" + bigInteger.toString(16));
    }

}
