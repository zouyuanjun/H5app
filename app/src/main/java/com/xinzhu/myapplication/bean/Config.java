package com.xinzhu.myapplication.bean;

public class Config {
    public int appname;
    public int ioc;
    public String version;
    public String homeurl;
    public String updataurl;

    public String getUpdataurl() {
        return updataurl;
    }

    public void setUpdataurl(String updataurl) {
        this.updataurl = updataurl;
    }

    public int getAppname() {
        return appname;
    }

    public void setAppname(int appname) {
        this.appname = appname;
    }

    public int getIoc() {
        return ioc;
    }

    public void setIoc(int ioc) {
        this.ioc = ioc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHomeurl() {
        return homeurl;
    }

    public void setHomeurl(String homeurl) {
        this.homeurl = homeurl;
    }
}
