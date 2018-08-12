package com.kumar.vikas.jecchat;

/**
 * Created by vikas on 27/12/17.
 */

public class Noti {

    private String from;
    private String type;
    private String key;
    private long time;

    public Noti(){}

    public Noti(String from, String type, String key, long time) {
        this.from = from;
        this.type = type;
        this.key =key;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
