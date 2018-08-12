package com.kumar.vikas.jecchat;

/**
 * Created by vikas on 4/1/18.
 */

public class post {

    public String from;
    public String message;
    public String image;
    public String video;
    public String file;
    public String time;
    public String type;
    public String likes;
    public String comments;
    public String push_key;
    public String fileName;
    public String college;

    public post(String from, String message, String image, String file, String fileName, String video, String time, String type, String likes, String comments, String push_key, String college) {
        this.from = from;
        this.message = message;
        this.image= image;
        this.video=video;
        this.file=file;
        this.time = time;
        this.type = type;
        this.likes = likes;
        this.comments = comments;
        this.push_key = push_key;
        this.fileName=fileName;
        this.college=college;
    }

    public post(){}

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPush_key() {
        return push_key;
    }

    public void setPush_key(String push_key) {
        this.push_key = push_key;
    }

    public String getLikes() {

        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }




    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
