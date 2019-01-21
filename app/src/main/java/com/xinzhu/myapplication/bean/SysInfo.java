package com.xinzhu.myapplication.bean;

public class SysInfo {
    String mode;  //手机型号
    String sysversion;  //系统版本
    String product;  //生产厂商
    int  sdk; //sdk版本

    public SysInfo(String mode, String sysversion, String product, int sdk) {
        this.mode = mode;
        this.sysversion = sysversion;
        this.product = product;
        this.sdk = sdk;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSysversion() {
        return sysversion;
    }

    public void setSysversion(String sysversion) {
        this.sysversion = sysversion;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getSdk() {
        return sdk;
    }

    public void setSdk(int sdk) {
        this.sdk = sdk;
    }
}
