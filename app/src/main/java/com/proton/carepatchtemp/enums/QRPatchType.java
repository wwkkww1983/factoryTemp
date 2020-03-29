package com.proton.carepatchtemp.enums;

//用于扫描体温贴二维码时候，判断类型
public enum QRPatchType {

    /**
     * 备注   医院（P10）和医院（P11）两个的类型是一样的，都是P0302,所以这里只取其中一个做判断即可
     */
    P03("P03", "旗舰版"), P04("P04", "专业版"), P05("P05", "简装版"), P07("P0406", "润生（P07）"), P10("P0302", "医院（P10）");
    private String type;
    private String des;

    QRPatchType(String type, String des) {
        this.type = type;
        this.des = des;
    }

    public String getType() {
        return type;
    }

    public String getDes() {
        return des;
    }

    public static QRPatchType getQRPatchType(String type) {
        for (QRPatchType qrPatchType :
                values()) {
            if (qrPatchType.getType().equalsIgnoreCase(type)) {
                return qrPatchType;
            }
        }
        return P03;
    }

}
