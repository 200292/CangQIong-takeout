package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("admin/common")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("upload")
    //该请求需要返回文件上传之后的绝对路径给前端回显
    public Result<String> upload(@RequestBody MultipartFile file){ //springmvc框架提供的处理文件上传操作的接口
        log.info("文件上传: {}",file);
        try {
            //获取原始文件名
            String filename = file.getOriginalFilename();
            //截取原始文件名的后缀加入到oss的对象名后
            String extension = filename.substring(filename.lastIndexOf("."));//获取最后一个点的索引，用substring获取点后的字符串
            String objectName = UUID.randomUUID().toString() + extension;
            //获取上传的文件在阿里云中的访问路径
            String path = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(path);
        } catch (IOException e) {
            log.error("文件上传失败 {}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
