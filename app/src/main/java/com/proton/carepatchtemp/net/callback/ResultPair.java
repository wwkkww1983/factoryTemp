package com.proton.carepatchtemp.net.callback;


import com.proton.carepatchtemp.utils.Constants;

public class ResultPair {

    private String ret = Constants.FAIL;
    private String data = "";

    public ResultPair() {
    }

    public ResultPair(String data) {
        this.data = data;
    }

    public ResultPair(String ret, String data) {
        this.ret = ret;
        this.data = data;
    }

    public boolean isSuccess() {
        return Constants.SUCCESS.equalsIgnoreCase(ret);
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ret : " + ret + " \n data : " + data;
    }
}
