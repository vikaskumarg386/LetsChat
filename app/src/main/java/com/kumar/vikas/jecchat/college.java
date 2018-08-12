package com.kumar.vikas.jecchat;

/**
 * Created by vikas on 7/7/18.
 */

public class college {

   private String name;
    private String address;

    public college(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
