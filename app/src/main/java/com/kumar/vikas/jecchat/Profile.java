package com.kumar.vikas.jecchat;

/**
 * Created by vikas on 25/3/18.
 */

public class Profile {

    String name;
    String fieldOfWork;
    String branch;
    String semester;
    String year;

    public Profile() {
    }

    public Profile(String name, String fieldOfWork, String branch, String semester, String year) {
        this.name = name;
        this.fieldOfWork = fieldOfWork;
        this.branch = branch;
        this.semester = semester;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldOfWork() {
        return fieldOfWork;
    }

    public void setFieldOfWork(String fieldOfWork) {
        this.fieldOfWork = fieldOfWork;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
