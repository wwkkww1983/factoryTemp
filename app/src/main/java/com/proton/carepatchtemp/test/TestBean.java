package com.proton.carepatchtemp.test;

public class TestBean {
    private int progress;
    private String progressSize;

    public TestBean(int progress) {
        this.progress = progress;
    }

    public TestBean(int progress, String progressSize) {
        this.progress = progress;
        this.progressSize = progressSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getProgressSize() {
        return progressSize;
    }

    public void setProgressSize(String progressSize) {
        this.progressSize = progressSize;
    }
}
