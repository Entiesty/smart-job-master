package com.fhxf.domain.constans;

public class Constants {
    public static final Integer REDIS_EXPIRED_ONE_MIN = 1000 * 60;
    public static final String REDIS_KEY_PREFIX = "smartJob:";
    public static final String REDIS_KEY_CHECK_CODE = REDIS_KEY_PREFIX + "checkCode:";
    public static final String REDIS_KEY_TOKEN = REDIS_KEY_PREFIX + "token:web:";
    public static final String REDIS_KEY_USER_ID = REDIS_KEY_PREFIX + "userId:";

    // token
    public static final String TOKEN_WEB = "token";
    public static final String TOKEN_ADMIN = "adminToken";
    // 文件
    public static final String FILE_PATH = "/files";
    public static final String FILE_IMAGE = "/images";
    public static final Integer FILE_NAME_LENGTH = 10;

}
