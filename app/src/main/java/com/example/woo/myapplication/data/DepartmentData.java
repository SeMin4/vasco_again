package com.example.woo.myapplication.data;

import java.io.Serializable;

public class DepartmentData implements Serializable {
    String checked;
    String u_department;
    String color;

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getDepartment() {
        return u_department;
    }

    public void setDepartment(String department) {
        this.u_department = department;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
