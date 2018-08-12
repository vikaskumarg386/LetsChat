package com.kumar.vikas.jecchat;

/**
 * Created by vikas on 17/3/18.
 */

public class reply {


    private String reply_Text;
    private String time;
    private String from;
    private String likes;
    private String type;
    private String cPush_key;
    private String push_key;
    private String video;
    private String file;
    private String image;
    private String reply;

    public String getPush_key() {
        return push_key;
    }

    public void setPush_key(String push_key) {
        this.push_key = push_key;
    }

    public reply(){}

    public reply(String reply_Text, String time, String from, String likes, String type, String cPush_key, String push_key, String video, String file, String image,String reply) {
        this.reply = reply_Text;
        this.time = time;
        this.from = from;
        this.likes = likes;
        this.type = type;
        this.cPush_key = cPush_key;
        this.push_key = push_key;
        this.video = video;
        this.file = file;
        this.image = image;
        this.reply=reply;
    }



    public String getreply_Text() {
        return reply_Text;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setReply_Text(String reply_Text) {
        this.reply_Text = reply_Text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getcPush_key() {
        return cPush_key;
    }

    public void setcPush_key(String cPush_key) {
        this.cPush_key = cPush_key;
    }
}
