package com.kumar.vikas.jecchat;

/**
 * Created by vikas on 31/12/17.
 */

public class chat {

    public long timeStamp;
    public boolean seen;
    private String lastMessage,college,fieldOfWork,year;

    public chat(){

    }


    public chat(long timeStamp, boolean seen, String lastMessage, String college, String fieldOfWork, String year) {
        this.timeStamp = timeStamp;
        this.seen = seen;
        this.lastMessage = lastMessage;
        this.college = college;
        this.fieldOfWork = fieldOfWork;
        this.year=year;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getFieldOfWork() {
        return fieldOfWork;
    }

    public void setFieldOfWork(String fieldOfWork) {
        this.fieldOfWork = fieldOfWork;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
