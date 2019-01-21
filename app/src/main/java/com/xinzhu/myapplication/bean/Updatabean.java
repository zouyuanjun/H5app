package com.xinzhu.myapplication.bean;

public class Updatabean {
    int versionCode;      //最新版本号
    String upateExplain; //更新说明
    int forceUpdata;         //是否为强制更新。1：是，0：不是
    String downloadUrl;  //下载地址
    public Updatabean() {

    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpateExplain() {
        return upateExplain;
    }

    public void setUpateExplain(String upateExplain) {
        this.upateExplain = upateExplain;
    }

    public int getForceUpdata() {
        return forceUpdata;
    }

    public void setForceUpdata(int forceUpdata) {
        this.forceUpdata = forceUpdata;
    }
}
