package com.proton.temp.connector.bean;

/**
 * Created by wangmengsi on 2018/08/13.
 * mqtt服务器配置
 */
public class MQTTConfig {
    private String serverUrl;
    private String username;
    private String password;

    public MQTTConfig() {
    }

    public MQTTConfig(String serverUrl, String username, String password) {
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
