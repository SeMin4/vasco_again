package com.example.woo.myapplication.data;

import java.io.Serializable;

public class Not_Complete_Data implements Serializable {
    String ul_longitude;
    String ul_latitude;
    String ul_desc;
    String ul_file;

    public String getUl_longitude() {
        return ul_longitude;
    }

    public void setUl_longitude(String ul_longitude) {
        this.ul_longitude = ul_longitude;
    }

    public String getUl_latitude() {
        return ul_latitude;
    }

    public void setUl_latitude(String ul_latitude) {
        this.ul_latitude = ul_latitude;
    }

    public String getUl_desc() {
        return ul_desc;
    }

    public void setUl_desc(String ul_desc) {
        this.ul_desc = ul_desc;
    }

    public String getUl_file() {
        return ul_file;
    }

    public void setUl_file(String ul_file) {
        this.ul_file = ul_file;
    }

}
