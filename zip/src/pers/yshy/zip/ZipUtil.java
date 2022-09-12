package pers.yshy.zip;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * zip的测试
 *
 * @author ysy
 * @since 2022/9/11
 */
public class ZipUtil {

    public static void main(String[] args) {
        String inFlePath = "zip/src/pers/yshy/zip.zip";
        String outPath = "zip/src/pers/yshy/1";
        try {
            ZipUtil zipUtil = new ZipUtil();
            zipUtil.unzipFle(inFlePath, outPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void zip() {

    }

//    /**
//     * 压缩文件成zip
//     *
//     * @param inFlePath 待压缩文件地址
//     * @param outPath   输出地址
//     * @throws IOException 异常
//     */
//    private static void zipFle(String inFlePath, String outPath) throws IOException {
//        // 判断待压缩文件是否存在，不存在直接结束
//        File inFle = new File(inFlePath);
//        if (!inFle.exists()) {
//            System.out.println("文件不存在");
//            return;
//        }
//        // 判断解压目录是否存在，不存在需要创建
//        File outFle = new File(outPath);
//        if (!outFle.exists()) {
//            boolean mkdirs = outFle.mkdirs();
//            if (!mkdirs) {
//                System.out.println("输出目录创建失败");
//                return;
//            }
//        }
//        // 组装文件名
//        String fleNme = inFle.getName();
//        String outFlePath = outPath + File.separator + fleNme.substring(0, fleNme.lastIndexOf(".")) + ".zip";
//
//        // 创建输入流
//        FileInputStream fis = new FileInputStream(inFle);
//        // 创建输出zip流
//        FileOutputStream fos = new FileOutputStream(outFlePath);
//        ZipOutputStream zos = new ZipOutputStream(fos);
//        // 创建ZipEntry对象，并将其加入zip输出流
//        ZipEntry zipEntry = new ZipEntry(inFle.getName());
//        zos.putNextEntry(zipEntry);
//        // 将输入流写入输出流
//        int len;
//        byte[] bytes = new byte[1024];
//        while ((len = fis.read(bytes)) != -1) {
//            zos.write(bytes, 0, len);
//        }
//        // 关闭流
//        fis.close();
//        zos.closeEntry();
//        zos.close();
//        fos.close();
//    }
//
//    /**
//     * 压缩文件夹成zip
//     *
//     * @param inFlePath 待压缩文件夹地址
//     * @param outPath   输出地址
//     * @throws IOException 异常
//     */
//    private static void zipDir(String inFlePath, String outPath) throws IOException {
//        // 判断待压缩文件是否存在，不存在直接结束
//        File inFle = new File(inFlePath);
//        if (!inFle.exists()) {
//            System.out.println("文件不存在");
//            return;
//        }
//        // 判断解压目录是否存在，不存在需要创建
//        File outFle = new File(outPath);
//        if (!outFle.exists()) {
//            boolean mkdirs = outFle.mkdirs();
//            if (!mkdirs) {
//                System.out.println("输出目录创建失败");
//                return;
//            }
//        }
//        // 组装文件名
//        String outFlePath = outPath + File.separator + inFle.getName() + ".zip";
//        // 创建输出zip流
//        FileOutputStream fos = new FileOutputStream(outFlePath);
//        ZipOutputStream zos = new ZipOutputStream(fos);
//        // 获取文件夹下所有文件
//        File[] files = inFle.listFiles();
//        for (File file : files) {
//            String flePath = file.getParent();
//            String fleNme = flePath.replace(inFle.getPath(), "") + File.separator + file.getName();
//            if (file.isDirectory()) {
//                // 如果输入文件是一个文件夹
//                zos.putNextEntry(new ZipEntry(fleNme + File.separator));
//            } else {
//                // 创建输入流
//                FileInputStream fis = new FileInputStream(file);
//                // 创建ZipEntry对象，并将其加入zip输出流
//                zos.putNextEntry(new ZipEntry(fleNme));
//                // 将输入流写入输出流
//                int len;
//                byte[] bytes = new byte[1024];
//                while ((len = fis.read(bytes)) != -1) {
//                    zos.write(bytes, 0, len);
//                }
//                // 关闭流
//                fis.close();
//            }
//        }
//        // 关闭流
//        zos.closeEntry();
//        zos.close();
//        fos.close();
//    }


    /**
     * zip模式压缩文件
     *
     * @param inFlePath 待压缩文件夹地址
     * @param outPath   输出地址
     * @throws IOException 异常
     */
    private void zipFle(String inFlePath, String outPath) throws IOException {
        // 校验输入输出地址
        File inFle = checkInFileAndOutFile(inFlePath, outPath);
        if (inFle == null) {
            return;
        }
        // 组装文件名
        String outFlePath = null;
        if (inFle.isDirectory()) {
            outFlePath = outPath + File.separator + inFle.getName() + ".zip";
        } else {
            String fleNme = inFle.getName();
            outFlePath = outPath + File.separator + fleNme.substring(0, fleNme.lastIndexOf(".")) + ".zip";
        }
        // 输出流
        ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(outFlePath)));
        // 压缩文件
        zipCompress(zos, inFle, inFle.getName());
        zos.closeEntry();
        zos.close();
    }

    /**
     * 根据文件的不同类型采用不同的操作
     *
     * @param zos      zip输出流
     * @param file     需要压缩的文件
     * @param fileName 文件名
     * @throws IOException 异常
     */
    private void zipCompress(ZipOutputStream zos, File file, String fileName) throws IOException {
        if (file.isDirectory()) {
            // file是文件夹，获得它下面的所有文件
            File[] files = file.listFiles();
            // 如果是空文件夹
            if (files == null) {
                zos.putNextEntry(new ZipEntry(fileName + File.separator));
                return;
            }
            // 不是空的文件夹
            for (File fle : files) {
                zipCompress(zos, fle, fileName + File.separator + fle.getName());
            }
        } else {
            // file不是文件夹，读取文件然后写入zip输出流
            // 创建输入流
            FileInputStream fis = new FileInputStream(file);
            // 创建ZipEntry对象，并将其加入zip输出流
            zos.putNextEntry(new ZipEntry(fileName));
            // 将输入流写入输出流
            int len;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) != -1) {
                zos.write(bytes, 0, len);
            }
            // 关闭流
            fis.close();
        }
    }

    private void unzipFle(String inFlePath, String outPath) throws IOException {
        // 校验输入输出地址
        File inFle = checkInFileAndOutFile(inFlePath, outPath);
        if (inFle == null) {
            return;
        }
        // 获取zip对象及其内部文件，这里为了防止中文名称的文件报错，需要设置编码
        ZipFile zipFile = new ZipFile(inFle, Charset.forName("GBK"));
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        // 循环获取文件
        while (entries.hasMoreElements()) {
            // 获取ZipEntry对象及其输入流
            ZipEntry zipEntry = entries.nextElement();
            InputStream is = zipFile.getInputStream(zipEntry);
            // 判断文件夹是否存在，不存在需要创建
            String name = zipEntry.getName();
            String path = Paths.get(outPath) + File.separator ;
            if(zipEntry.isDirectory()) {
                path += name;
            } else {
                path += name.substring(0, name.lastIndexOf("/"));
            }

            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }
            // 如果是一个文件夹，则不需要操作，如果是文件，就需要输出了
            if(!zipEntry.isDirectory()) {
                FileOutputStream fos = new FileOutputStream(Paths.get(outPath) + File.separator + zipEntry.getName());
                int len;
                byte[] bytes = new byte[1024];
                while ((len = is.read(bytes)) != -1) {
                    fos.write(bytes, 0, len);
                }
                is.close();
                fos.close();
            }
        }
    }

    /**
     * 校验输入输出地址
     *
     * @param inFlePath 输入地址
     * @param outPath   输出地址
     * @return 输入文件
     */
    private File checkInFileAndOutFile(String inFlePath, String outPath) {
        // 判断待压缩文件是否存在，不存在直接结束
        File inFle = new File(inFlePath);
        if (!inFle.exists()) {
            System.out.println("文件不存在");
            return null;
        }
        // 判断解压目录是否存在，不存在需要创建
        File outFle = new File(outPath);
        if (!outFle.exists()) {
            boolean mkdirs = outFle.mkdirs();
            if (!mkdirs) {
                System.out.println("输出目录创建失败");
                return null;
            }
        }
        return inFle;
    }
}
