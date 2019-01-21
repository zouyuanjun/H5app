package com.xinzhu.myapplication.event;

public class SaveData {
    String key;
    String valu;

    public SaveData(String key, String valu) {
        this.key = key;
        this.valu = valu;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValu() {
        return valu;
    }

    public void setValu(String valu) {
        this.valu = valu;
    }
}
