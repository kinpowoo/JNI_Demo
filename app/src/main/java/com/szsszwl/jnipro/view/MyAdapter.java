package com.szsszwl.jnipro.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szsszwl.jnipro.R;

import java.util.List;

/**
 * Created by DeskTop29 on 2018/8/16.
 */

public class MyAdapter extends MyBaseAdapter {
    List<String> list;
    Context c;
    LayoutInflater inflater;
    float cellWidth,horizontalSpace;
    int rowCount;

    public MyAdapter(Context context, List<String> data){
        this.list = data;
        this.c = context;
        this.inflater = LayoutInflater.from(c);
        this.rowCount = 3;
        this.horizontalSpace = 0;
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getCellWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(layoutParams);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.tv);
        tv.setText( list.get(position));
        return convertView;
    }

    @Override
    public int getCellWidth() {
        calCellWidth();
        return (int)cellWidth;
    }

    @Override
    void calCellWidth() {
        float sw = getScreenWidth(c);
        cellWidth = (sw - (rowCount-1)*horizontalSpace)/rowCount;
    }

    @Override
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;

    }

    @Override
    public void setHorizontalMargin(float horizontalMargin) {
        this.horizontalSpace = (int)horizontalMargin;
    }
}
