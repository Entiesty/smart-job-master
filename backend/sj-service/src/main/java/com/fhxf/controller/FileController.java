package com.fhxf.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.fhxf.domain.constans.Constants;
import com.fhxf.global.ResponseCodeEnum;
import com.fhxf.global.ResponseResult;
import com.fhxf.global.exception.BusinessException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @className: FileController
 * @author: 李昌泉
 * @date: 2024/11/17 下午10:53
 * @Version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/file")
@Validated
@Slf4j
public class FileController extends ABaseController {


    @RequestMapping("/uploadImage")
    public ResponseResult uploadimage(@NotNull MultipartFile file, boolean createThumbnail) throws IOException, BusinessException {
        String fileDayFolder = DateUtil.format(new Date(), "yyMM");
        File uploadDir = new File(appconfig.getProjectFolder() +  Constants.FILE_IMAGE + "/" + fileDayFolder);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String fileName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        Path path = Paths.get(uploadDir.getPath(), RandomUtil.randomString(Constants.FILE_NAME_LENGTH) + fileName);
        file.transferTo(path);
        return success(fileDayFolder + "/" + path.getFileName());
    }

    /**
     * 根据图片路径获取图片资源
     *
     * @param response 用于输出图片资源的HttpServletResponse对象
     * @param path  图片文件的路径
     * @return ResponseResult对象，包含请求的结果信息
     * @throws IOException       如果文件读取过程中发生I/O错误
     * @throws BusinessException 如果文件路径不合法，抛出业务异常
     */
    @RequestMapping("/getImage")
    public ResponseResult getimage(HttpServletResponse response, String path) throws IOException, BusinessException {
        if(StrUtil.isEmpty(path)){
            readFile(response, appconfig.getProjectFolder() + Constants.FILE_IMAGE +"/default.jpg");
            return success();
        }
        path = URLDecoder.decode(path, "UTF-8");
        if (StrUtil.isEmpty(path)) {
            return response(ResponseCodeEnum.NOT_FOUND.getCode(), false, ResponseCodeEnum.NOT_FOUND.getMessage(), null);
        }
        // 验证文件路径是否合法
        if (StrUtil.contains(path, "../") || StrUtil.contains(path, "..\\")) {
            return response(ResponseCodeEnum.CONFLICT.getCode(), false, ResponseCodeEnum.CONFLICT.getMessage());
        }
        // 设置响应内容类型为图片类型，并根据文件后缀设置具体类型
        response.setContentType("image/" +path.substring(path.lastIndexOf('.')+1));
        // 设置缓存策略，图片资源在客户端缓存30天
        response.setHeader("Cache-Control", "max-age=2592000");
        // 读取文件并输出到response
        readFile(response, appconfig.getProjectFolder() +Constants.FILE_IMAGE + "/" + path);
        // 返回成功响应
        return success();
    }

    protected void readFile(HttpServletResponse response, String filePath) throws BusinessException {

        File file = new File(filePath);
        if (!file.exists()) {
            throw new BusinessException("文件不存在");
        }
        if (appconfig.isShowlog()) {
            log.info("文件下载：" + filePath);
        }
        try (ServletOutputStream outputStream = response.getOutputStream();
             FileInputStream inputStream = new FileInputStream(file);) {
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("文件读取异常", e);
        }
    }

    @RequestMapping("/uploadFile")
    public ResponseResult uploadFile( @NotNull MultipartFile file) throws IOException, BusinessException {
        String fileDayFolder = DateUtil.format(new Date(), "yyMM");
        File uploadDir = new File(appconfig.getProjectFolder() + Constants.FILE_PATH +  "/" + fileDayFolder);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String fileName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        Path path = Paths.get(uploadDir.getPath(), RandomUtil.randomString(Constants.FILE_NAME_LENGTH) + fileName);
        file.transferTo(path);
        return success(fileDayFolder + "/" + path.getFileName());
    }
    @RequestMapping("/getFile")
    public ResponseResult getFile(HttpServletResponse response, String path) throws IOException, BusinessException {
        if(StrUtil.isEmpty(path)){
            return fail();
        }
        path = URLDecoder.decode(path, "UTF-8");
        if (StrUtil.isEmpty(path)) {
            return response(ResponseCodeEnum.NOT_FOUND.getCode(), false, ResponseCodeEnum.NOT_FOUND.getMessage(), null);
        }
        // 验证文件路径是否合法
        if (StrUtil.contains(path, "../") || StrUtil.contains(path, "..\\")) {
            return response(ResponseCodeEnum.CONFLICT.getCode(), false, ResponseCodeEnum.CONFLICT.getMessage());
        }
        // 设置响应内容类型为图片类型，并根据文件后缀设置具体类型
        response.setContentType("application/" +path.substring(path.lastIndexOf('.')+1));
        // 设置缓存策略，图片资源在客户端缓存30天
        response.setHeader("Cache-Control", "max-age=2592000");
        // 读取文件并输出到response
        readFile(response, appconfig.getProjectFolder() + Constants.FILE_PATH  + "/" + path);
        // 返回成功响应
        return success();
    }

}

