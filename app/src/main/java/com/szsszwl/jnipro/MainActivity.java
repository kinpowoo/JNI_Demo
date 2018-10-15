package com.szsszwl.jnipro;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.szsszwl.jnipro.view.HorizontalAutoScrollLayout;
import com.szsszwl.jnipro.view.MyAdapter;
import com.szsszwl.jnipro.view.NestFullListView;
import com.szsszwl.jnipro.view.NestFullListViewAdapter;
import com.szsszwl.jnipro.view.NestFullViewHolder;
import com.szsszwl.jnipro.view.SwitchTabView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    List<String> data;
    MyAdapter myAdapter;
    HorizontalAutoScrollLayout horizontalScrollView;

    Button start,stop,goHost,goNative,goException;
    LinearLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontalScrollView = (HorizontalAutoScrollLayout) findViewById(R.id.has);
        container = (LinearLayout) findViewById(R.id.container);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        goHost = (Button) findViewById(R.id.go_host);
        goNative = (Button) findViewById(R.id.go_native);
        goException = (Button) findViewById(R.id.go_exception);


        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        goHost.setOnClickListener(this);
        goNative.setOnClickListener(this);
        goException.setOnClickListener(this);

        data = new ArrayList<>();
        for(int i=0;i<10;i++){
            data.add("我是第"+i+"个item");
        }

        myAdapter = new MyAdapter(this,data);
        myAdapter.setHorizontalMargin(horizontalScrollView.getHorizontalMargin());
        myAdapter.setRowCount(horizontalScrollView.getRowCount());

        horizontalScrollView.setAdapter(myAdapter);
        horizontalScrollView.setOnItemChangeListener(new HorizontalAutoScrollLayout.OnItemChangeListener() {
            @Override
            public void onItemChange(View v, int pos) {

            }
        });



        FrameLayout v = SwitchTabView.get().initView(this,4,500,new String[]{"我是歌王","绝地求生","英雄联盟","穿越火线"});
        SwitchTabView.get().setTabSwitchListener(new SwitchTabView.TabSwitchListener() {
            @Override
            public void tabSwitch(TextView tv, int position) {
                Log.i("tag","tab现在的下标为:"+position);
            }
        });

        SwitchTabView.get().setBgColor(Color.parseColor("#FF0000"));
        SwitchTabView.get().setBgDrawable(getResources().getDrawable(R.drawable.out_border));
        SwitchTabView.get().setSliderDrawable(getResources().getDrawable(R.drawable.inner_border));

        container.addView(v);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                horizontalScrollView.switchPlayFlag();
                break;
            case R.id.stop:
                horizontalScrollView.switchPlayFlag();
                break;
            case R.id.go_host:
                startActivity(new Intent(this,TabTestActivity.class));
                break;
            case R.id.go_native:
                startActivity(new Intent(this,LocalRefOverflowActivity.class));
                break;
            case R.id.go_exception:
                startActivity(new Intent(this,ExceptionThrowActivity.class));
                break;
        }
    }

    public native String stringFromJNI();
}
