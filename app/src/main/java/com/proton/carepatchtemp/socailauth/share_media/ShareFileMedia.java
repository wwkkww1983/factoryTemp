package com.proton.carepatchtemp.socailauth.share_media;

public class ShareFileMedia implements IShareMedia {
    private String title;
    private String filePath;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
