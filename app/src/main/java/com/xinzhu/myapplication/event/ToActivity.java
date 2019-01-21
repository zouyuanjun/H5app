package com.xinzhu.myapplication.event;

public class ToActivity {
    public int tag;
    public String message;

    public ToActivity(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public ToActivity(int tag, String message) {
        this.tag = tag;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
