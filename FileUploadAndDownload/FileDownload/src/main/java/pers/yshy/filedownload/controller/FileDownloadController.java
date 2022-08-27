package pers.yshy.filedownload.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传的控制器
 *
 * @author ysy
 * @since 2022/8/18
 */
@RequestMapping("/file")
@RestController
public class FileDownloadController {

    @PostMapping("/upload")
    public Object fileUpload(@RequestPart("req_file") MultipartFile file, @RequestPart("req_msg") String msg) {
        System.out.println("==================== 请求参数：" + msg);
        saveFile(file);
        Map<String, String> resMap = new HashMap<>();
        resMap.put("res_code", "0000");
        resMap.put("res_code_des", "Success");
        return resMap;
    }

    private void saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            System.out.println("==================== 文件不存在");
            return;
        }
        String fileName = file.getOriginalFilename();
        System.out.println("==================== 文件名：" + fileName);
        // 文件新地址，如果没有，就创建
        String filePath = "src/main/resources";
        File newFile = new File(filePath);
        if (!newFile.exists()) {
            newFile.mkdir();
        }

        try {
            String reportPath = newFile.getCanonicalPath();
            System.out.println("==================== 上传地址：" + reportPath + "\\static\\file\\" + fileName);
            file.transferTo(new File(reportPath + "\\static\\file\\" + fileName));
            System.out.println("==================== 上传成功");
        } catch (IOException e) {
            System.out.println("==================== 上传失败");
            e.printStackTrace();
        }


    }
}
