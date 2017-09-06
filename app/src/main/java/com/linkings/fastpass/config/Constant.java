package com.linkings.fastpass.config;

/**
 * Created by Lin on 2017/9/4.
 * Time: 17:50
 * Description: TOO
 */

public class Constant {

    /**
     * 默认的Wifi SSID
     */
    public static final String DEFAULT_SSID = "FastPass";
    
    /**
     * UDP通信服务 默认端口
     */
    public static final int DEFAULT_SERVER_COM_PORT = 8989;

    /**
     * Wifi连接上时 未分配默认的Ip地址
     */
    public static final String DEFAULT_UNKOWN_IP = "0.0.0.0";

    /**
     * 最大尝试数
     */
    public static final int DEFAULT_TRY_TIME = 10;

    public static final String KEY_IP_PORT_INFO = "KEY_IP_PORT_INFO";

    /**
     * UDP通知：文件接收端初始化
     */
    public static final String MSG_FILE_RECEIVER_INIT = "MSG_FILE_RECEIVER_INIT";

    /**
     * UDP通知：文件接收端初始化完毕
     */
    public static final String MSG_FILE_RECEIVER_INIT_SUCCESS = "MSG_FILE_RECEIVER_INIT_SUCCESS";

    /**
     * UDP通知：开始发送文件
     */
    public static final String MSG_START_SEND = "MSG_START_SEND";

}
