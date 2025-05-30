package com.fhxf.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @className: Appconfig
 * @author: 李昌泉
 * @date: 2024/11/16 下午8:04
 * @Version: 1.0
 * @description:
 */
@Configuration
@Data
public class Appconfig {
    @Value("${project.folder}")
    private String projectFolder;
//    @Value("${admin.account}")
//    private String account;
//    @Value("${admin.password}")
//    private String password;
    @Value("${ffmpeg.showLog}")
    private boolean showlog;
//    @Value("${es.host.port:127.0.0.1:9200}")
//    private String esHostPort;
//    @Value("${es.index.video.name:easylive_video}")
//    private String esIndexVideoName;
}
