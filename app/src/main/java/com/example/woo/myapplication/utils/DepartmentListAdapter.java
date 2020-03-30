package com.example.woo.myapplication.utils;

import android.view.View;
import android.widget.BaseAdapter;

public class DepartmentListAdapter {//extends BaseAdapter implements View.OnClickListener {{
    public interface ListBtnClickListener{
        void onListBtnClick(int position);
    }

//    public ArrayList<> myRoomList = new ArrayList<>();
    private ListBtnClickListener listBtnClickListener;

//    @Override
//    public int getCount(){
//        return 1;
//    }
}
