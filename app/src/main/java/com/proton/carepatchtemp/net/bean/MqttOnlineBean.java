package com.proton.carepatchtemp.net.bean;

/**
 * Created by luochune on 2018/4/26.
 */

public class MqttOnlineBean {

    /**
     * ipaddress : 36.24.225.45
     * port : 30241
     * clean_sess : true
     * keepalive : 60
     * proto_ver : 4
     * online : 1
     * connected_at : 2018-04-24 11:06:56
     * client_id : proton372
     * username : proton
     */

    private String ipaddress;
    private int port;
    private boolean clean_sess;
    private int keepalive;
    private int proto_ver;
    private int online;
    private String connected_at;
    private String client_id;
    private String username;

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isClean_sess() {
        return clean_sess;
    }

    public void setClean_sess(boolean clean_sess) {
        this.clean_sess = clean_sess;
    }

    public int getKeepalive() {
        return keepalive;
    }

    public void setKeepalive(int keepalive) {
        this.keepalive = keepalive;
    }

    public int getProto_ver() {
        return proto_ver;
    }

    public void setProto_ver(int proto_ver) {
        this.proto_ver = proto_ver;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getConnected_at() {
        return connected_at;
    }

    public void setConnected_at(String connected_at) {
        this.connected_at = connected_at;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
