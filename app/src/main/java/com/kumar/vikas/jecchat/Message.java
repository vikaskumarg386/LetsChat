package com.kumar.vikas.jecchat;

/**
 * Created by vikas on 30/12/17.
 */

public class Message  {

    private String message,image,file,type,from,name;
    private String timestamp;
    private boolean seen;

    public Message(){}

    public Message(String message,String image ,String file, String type, String from,String name, String timestamp, boolean seen) {
        this.message = message;
        this.image=image;
        this.file=file;
        this.type = type;
        this.from=from;
        this.timestamp = timestamp;
        this.seen = seen;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void message(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFrom() {return from;}

    public void setFrom(String from) {this.from = from;}


}
