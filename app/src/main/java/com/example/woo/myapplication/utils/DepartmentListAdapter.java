package com.example.woo.myapplication.utils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.DepartmentData;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class DepartmentListAdapter extends BaseAdapter implements View.OnClickListener {
    public interface ListBtnClickListener{
        void onDepartListBtnClick(int position);
    }

    public ArrayList<DepartmentData> departList = new ArrayList<DepartmentData>();
    private ListBtnClickListener listBtnClickListener;

    public DepartmentListAdapter(ListBtnClickListener listBtnClickListener){
        this.listBtnClickListener = listBtnClickListener;
    }

    public DepartmentListAdapter(){

    }

    @Override
    public int getCount(){
        return departList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        // "depart_listview" 레이아웃 Inflate convertView
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.depart_listview, parent, false);
        }

        TextView departmentNameTextView = (TextView) convertView.findViewById(R.id.department_name);
        Button modifyButton = (Button) convertView.findViewById(R.id.department_modify);

        GradientDrawable drawable= (GradientDrawable) context.getDrawable(R.drawable.iv_circle);

        DepartmentData departmentData = departList.get(position);

        drawable.setColor(parseInt(departmentData.getColor()));

        departmentNameTextView.setText(departmentData.getDepartment());
        modifyButton.setTag(position);
        modifyButton.setBackground(drawable);
        modifyButton.setClipToOutline(true);
        modifyButton.setOnClickListener(this);

        return convertView;
    }

    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public Object getItem(int position){
        return departList.get(position);
    }

    public void addItem(String departmentName, String color){
        DepartmentData data = new DepartmentData();
        data.setDepartment(departmentName);
        data.setColor(color);
        departList.add(data);
    }

    // modify 버튼 태그를 이용하여 처리
    public void onClick(View v){
        //MyPageActivity 의 onDepartListBtnClick()함수 호출
        if(this.listBtnClickListener != null){
            this.listBtnClickListener.onDepartListBtnClick((int)v.getTag());

        }
    }

}
