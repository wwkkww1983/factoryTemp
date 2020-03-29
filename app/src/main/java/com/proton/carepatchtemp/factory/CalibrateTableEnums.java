package com.proton.carepatchtemp.factory;

/**
 * @Description: 体温校准对照表
 * @Author: yxf
 * @CreateDate: 2020/3/27 11:27
 * @UpdateUser: yxf
 * @UpdateDate: 2020/3/27 11:27
 */
public enum CalibrateTableEnums {

    MINUS_030(-0.30f, "F0"),
    MINUS_029(-0.29f, "E9"),
    MINUS_028(-0.28f, "E8"),
    MINUS_027(-0.27f, "E7"),
    MINUS_026(-0.26f, "E6"),
    MINUS_025(-0.25f, "E5"),
    MINUS_024(-0.24f, "E4"),
    MINUS_023(-0.23f, "E3"),
    MINUS_022(-0.22f, "E2"),
    MINUS_021(-0.21f, "E1"),
    MINUS_020(-0.20f, "E0"),
    MINUS_019(-0.19f, "D9"),
    MINUS_018(-0.18f, "D8"),
    MINUS_017(-0.17f, "D7"),
    MINUS_016(-0.16f, "D6"),
    MINUS_015(-0.15f, "D5"),
    MINUS_014(-0.14f, "D4"),
    MINUS_013(-0.13f, "D3"),
    MINUS_012(-0.12f, "D2"),
    MINUS_011(-0.11f, "D1"),
    MINUS_010(-0.10f, "D0"),
    MINUS_009(-0.09f, "C9"),
    MINUS_008(-0.08f, "C8"),
    MINUS_007(-0.07f, "C7"),
    MINUS_006(-0.06f, "C6"),
    MINUS_005(-0.05f, "C5"),
    MINUS_004(-0.04f, "C4"),
    MINUS_003(-0.03f, "C3"),
    MINUS_002(-0.02f, "C2"),
    MINUS_001(-0.01f, "C1"),
    CENTER_0(0f, "80"),
    PLUS_001(0.01f, "81"),
    PLUS_002(0.02f, "82"),
    PLUS_003(0.03f, "83"),
    PLUS_004(0.04f, "84"),
    PLUS_005(0.05f, "85"),
    PLUS_006(0.06f, "86"),
    PLUS_007(0.07f, "87"),
    PLUS_008(0.08f, "88"),
    PLUS_009(0.09f, "89"),
    PLUS_010(0.10f, "90"),
    PLUS_011(0.11f, "91"),
    PLUS_012(0.12f, "92"),
    PLUS_013(0.13f, "93"),
    PLUS_014(0.14f, "94"),
    PLUS_015(0.15f, "95"),
    PLUS_016(0.16f, "96"),
    PLUS_017(0.17f, "97"),
    PLUS_018(0.18f, "98"),
    PLUS_019(0.19f, "99"),
    PLUS_020(0.20f, "A0"),
    PLUS_021(0.21f, "A1"),
    PLUS_022(0.22f, "A2"),
    PLUS_023(0.23f, "A3"),
    PLUS_024(0.24f, "A4"),
    PLUS_025(0.25f, "A5"),
    PLUS_026(0.26f, "A6"),
    PLUS_027(0.27f, "A7"),
    PLUS_028(0.28f, "A8"),
    PLUS_029(0.29f, "A9"),
    PLUS_030(0.30f, "B0");
    /**
     * 校准温度值
     */
    private float t;
    /**
     * 需要写入的十六进制
     */
    private String tHex;

    CalibrateTableEnums(float t, String tHex) {
        this.t = t;
        this.tHex = tHex;
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    public String gettHex() {
        return tHex;
    }

    public void settHex(String tHex) {
        this.tHex = tHex;
    }

    /**
     * 通过校准值获取CalibrateTableEnums对象
     *
     * @return
     */
    public static CalibrateTableEnums getCalibrateTByT(float t) {
        for (CalibrateTableEnums c : values()) {
            if (t == c.getT()) {
                return c;
            }
        }
        return CENTER_0;
    }

    @Override
    public String toString() {
        return "CalibrateTableEnums{" +
                "t=" + t +
                ", tHex='" + tHex + '\'' +
                '}';
    }
}
