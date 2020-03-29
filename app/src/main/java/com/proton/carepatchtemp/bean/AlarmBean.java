package com.proton.carepatchtemp.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by yuxiongfeng.
 * Date: 2019/8/28
 */
public class AlarmBean extends LitePalSupport {
    private String uid;
    private String name;
    private String fileName;
    /***
     * 1:表示选中  0：表示未选中
     */
    private int isSelected;

    public AlarmBean(String uid, String name, String fileName, int isSelected) {
        this.uid = uid;
        this.name = name;
        this.fileName = fileName;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "AlarmBean{" +
                "name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
