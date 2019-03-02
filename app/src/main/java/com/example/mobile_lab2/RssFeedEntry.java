package com.example.mobile_lab2;

public class RssFeedEntry {
    private String mUrl;
    private boolean mIsMarked;

    public RssFeedEntry(String url, boolean state) {
        this.mUrl = url;
        this.mIsMarked = state;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public boolean getIsMarked() {
        return this.mIsMarked;
    }

    public void setIsMarked(boolean state) {
        this.mIsMarked = state;
    }
}
