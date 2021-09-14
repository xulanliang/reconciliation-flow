

/**

 * Alipay.com Inc.

 * Copyright (c) 2004-2014 All Rights Reserved.

 */

package com.yiban.rec.util;


/**
 * 支付宝服务窗环境常量（demo中常量只是参考，需要修改成自己的常量值）
 * 
 * @version 
 */
public class AlipayServiceEnvConstants {

    /**支付宝公钥-从支付宝页面获取*/
	public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoFN7yWUsJtRx9qnMVdFfHqVmQuXA/Viuw+I18PfJopeznORb2kfzESJs6lB5pIgLu8Y04nL8Q3RAgtgjruJv60WKJyQ45RiNuuoTRIgd5xXP+vV3Pv4v81NHEcFZ9Jd9N2X/DX9FkXl810cVxq/ZaRtCrSxB4MfA9vEFvVLOCBF/sCtFmuB7fTWFtgD6hzgONIm5VBzcF8HmKVqdWY9WqtOi9AApGrUFC9coRJf+Zfko4vQN0mLRCeDyN4CnpEjFNtm+g/mb8yDe4RpVjHPXZXMMQ/leWzPLMeb2ygbD/9IzB7Yo+hrS7Z6Ez2KVmOsbn2jHzVfLf1vDQ9s57AbX3wIDAQAB";
    
    /**签名编码*/
    public static final String SIGN_CHARSET      = "GBK";

    /**字符编码-传递给支付宝的数据编码*/
    public static final String CHARSET           = "GBK";

    /**签名类型*/
    public static final String SIGN_TYPE         = "RSA2";
    
    /**开发者账号PID*/
    public static final String PARTNER           = "";

    /** appId  */
    //TODO !!!! 注：该appId必须设为开发者自己
    public static final String APP_ID            = "2018011601908160";

    //TODO !!!! 注：该私钥为测试账号私钥  开发者必须设置自己的私钥 , 否则会存在安全隐患 
    public static final String PRIVATE_KEY       = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCxh61W+hizvnXPaM4Xgn7S7kNGVkXJDHT026vmKUSlhZSga0fFQRkNnZ2TWzTdKXgEFV3b3k8DyNSiCGDBlfizCoAypNOvkEkgpD0TdKAgebDRrADs/Snyfm1g4OvAgTz3QcNgwlxtaNgUsiid8LlVGpM7EB2W9dTvX/d/UWWJbJdJL6AvvANbk403YPxH6AYDghMRBgbaJtvba5UOREKaH35pnYicOzGjM7FzFAthOhq5+A2bdc5BHXjOMHJ3sehemZmjjOivn7XK7EtG7uVQn3GDUjCvdT+oLWNbUQ56DIpGHT4SAKkJzfu+dew78gei2Eam76y8hFDf5utigJRVAgMBAAECggEAM4fNcdSwfOQI8EZcpAhV5cYRMwZxxTTfcf+devZ5nY4ToSjisUf/DKu2hIJR6uxRkOXe06ZIyzFtPwcthqOQh+/BXHHpnClGgLoLnG0a2bJY99N0hVXK6j92YkuHQt6Aosis/JYOHBf1uLQqBAZO1XwcRmWcISiEQWNaxNMMpQGqU1yp4sBuyntRx/L2vSsV42Ijubyv6V2uh/KBJOFIRFqtYeRLUdSKCX95xlVXPriaJJ7jPeQ6L8uuR7dUVdBlnVeB0oOEpD6sT2nvi8C3V1fAU50CSp7jeEX1+wG+GpwcQMaBx3stEbkRUbtVljMOaTUqY+1Unw3CWqocwS60wQKBgQD0RJBiaquggoLdHSw0Y27M5oolbSZI3Xr5+woG+tkoJppXP7XBMk3E9QNsiR2myQmk8Nh/svGpfK8BCKG3vVbm237aSs2G11o1vP2u+ZljNddytXytdUsK+4d5ADtolXtcvt6njzcTQGAmThy59bJ2B1bKqEwk2YEAoR1sFQw+0QKBgQC6Dobd2nT8ffvra0ACLLcWAo0dkEZD6g1GXiq3AhDo7tS6555dtEt+VgWzJWAUXFU2zVJRRbBZoK1QGA5+Qw8uYfGek9/ybyo91FTEI+G4M9nieKhdRNWLSLUsN3ri8b0vdElHcuiHmqY4O3cs2Al9HypJLaYzki7GfVuSVz7GRQKBgDNVmXZlbo47/16Su/CCmQ2Mwamd57bWfPq+LFxNKYgLnNILPJpOhROIXmDX7BhaFaHJT+Z+Z9Uzs9+mvb3Ml8/Kfz1HDSxyY635euLj57122kFsLLiTduLm+5CNYO7Yw2U5Y7eZHHz6QXqrXdQi849keXP7rIzt/PNx6KoHPlyRAoGAFgB9xmJ9Dxa+4boPGbK7++hVU7ep94IIC9g66OVfpowHsAqP2bjmbIwzpFm5IO9CTHa/U0kQb6jmleAiZp/7fZqrffsysnKe5izj0UYE/Ul0bKmkD1OChTQV6KKu74NywdfYipB2GwMYwPGXSEBL7vXuhDTXwKV6zpIocAfNLpkCgYA925iMD9pjVkgEsn7LueZIPW6TI19UYTq1SPVqMcxq0XZ6ssJmvGXEJNXOA5ySSyXB2fZlLEOaQNO7G7i0AJqQ8SSoW7Y5xOEEryeSFnxkArjLSKKe6XhxRgB2yd67aY1Mr1MpuMVpJP/uwd1+nwpo8rqrz0xvPisr28yoTtYeCA==";
    
    //TODO !!!! 注：该公钥为测试账号公钥  开发者必须设置自己的公钥 ,否则会存在安全隐患
    public static final String PUBLIC_KEY        = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsYetVvoYs751z2jOF4J+0u5DRlZFyQx09Nur5ilEpYWUoGtHxUEZDZ2dk1s03Sl4BBVd295PA8jUoghgwZX4swqAMqTTr5BJIKQ9E3SgIHmw0awA7P0p8n5tYODrwIE890HDYMJcbWjYFLIonfC5VRqTOxAdlvXU71/3f1FliWyXSS+gL7wDW5ONN2D8R+gGA4ITEQYG2ibb22uVDkRCmh9+aZ2InDsxozOxcxQLYToaufgNm3XOQR14zjByd7HoXpmZo4zor5+1yuxLRu7lUJ9xg1Iwr3U/qC1jW1EOegyKRh0+EgCpCc37vnXsO/IHothGpu+svIRQ3+brYoCUVQIDAQAB";
    												/**支付宝网关*/
    public static final String ALIPAY_GATEWAY    = "https://openapi.alipay.com/gateway.do";

    /**授权访问令牌的授权类型*/
    public static final String GRANT_TYPE        = "authorization_code";
    
    /**AES对称加密密钥*/
    public static final String AES_KEY			 = "DbKeFEoBxVt0iO+Jd2iqhg==";
}