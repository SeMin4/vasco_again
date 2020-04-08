package com.example.woo.myapplication.data;

import java.io.Serializable;
import java.util.ArrayList;

public class MapInfo implements Serializable {
    private String m_id;
    private String p_id;
    private String m_password;
    private String m_owner;
    private String m_status;     // 현재 수색작업 현황(시작전/수색중/수색완료...등등)
    private String m_size;
    private String m_unit_scale;
    private String m_rotation;   // 지도 기울기
    private String m_center_place_string;    // 지도 중심 위치 string
    private String m_center_point_latitude;    // 지도 중심점
    private String m_center_point_longitude;
    private String m_find_latitude;
    private String m_find_longitude;
    private ArrayList<Integer> placeIndex;



    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getM_password() {
        return m_password;
    }

    public void setM_password(String m_password) {
        this.m_password = m_password;
    }

    public String getM_owner() {
        return m_owner;
    }

    public void setM_owner(String m_owner) {
        this.m_owner = m_owner;
    }

    public String getM_status() {
        return m_status;
    }

    public void setM_status(String m_status) {
        this.m_status = m_status;
    }

    public String getM_size() {
        return m_size;
    }

    public void setM_size(String m_size) {
        this.m_size = m_size;
    }

    public String getM_unit_scale() {
        return m_unit_scale;
    }

    public void setM_unit_scale(String m_unit_scale) {
        this.m_unit_scale = m_unit_scale;
    }

    public String getM_rotation() {
        return m_rotation;
    }

    public void setM_rotation(String m_rotation) {
        this.m_rotation = m_rotation;
    }

    public String getM_center_place_string() {
        return m_center_place_string;
    }

    public void setM_center_place_string(String m_center_place_string) {
        this.m_center_place_string = m_center_place_string;
    }

    public String getM_center_point_latitude() {
        return m_center_point_latitude;
    }

    public void setM_center_point_latitude(String m_center_point_latitude) {
        this.m_center_point_latitude = m_center_point_latitude;
    }

    public String getM_center_point_longitude() {
        return m_center_point_longitude;
    }

    public void setM_center_point_longitude(String m_center_point_longitude) {
        this.m_center_point_longitude = m_center_point_longitude;
    }

    public String getM_find_latitude() {
        return m_find_latitude;
    }

    public void setM_find_latitude(String m_find_latitude) {
        this.m_find_latitude = m_find_latitude;
    }

    public String getM_find_longitude() {
        return m_find_longitude;
    }

    public void setM_find_longitude(String m_find_longitude) {
        this.m_find_longitude = m_find_longitude;
    }

    public ArrayList<Integer> getPlaceIndex() {
        return placeIndex;
    }

    public void setPlaceIndex(ArrayList<Integer> placeIndex) {
        this.placeIndex = placeIndex;
    }
}
