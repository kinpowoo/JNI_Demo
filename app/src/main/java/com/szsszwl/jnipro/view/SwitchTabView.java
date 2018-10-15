package com.szsszwl.jnipro.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.szsszwl.jnipro.R;

/**
 * Created by DeskTop29 on 2018/8/21.
 */

public class SwitchTabView implements View.OnClickListener{

    int w,h,count,currentIndex,singleW;
    int paddingSpace = 10;

    TextView[] tabs;
    String[] titles;
    int [] tabsId;

    int bgColor = Color.parseColor("#FFFAFA");
    int sliderColor = Color.WHITE;
    int textSelectedColor = Color.BLACK;
    int textUnselectedColor = Color.LTGRAY;


    FrameLayout frameLayout;
    View slider;
    TabSwitchListener tabSwitchListener;


    static SwitchTabView instance;
    private SwitchTabView(){}


    public static SwitchTabView get(){
        if(instance == null){
            synchronized (SwitchTabView.class){
                instance = new SwitchTabView();
            }
        }
        return instance;
    }

    public void setTabSwitchListener(TabSwitchListener tabSwitchListener){
        this.tabSwitchListener = tabSwitchListener;
    }

    public FrameLayout initView(Context c,int count,String[] titles) {
        return this.initView(c,count,200,50,titles);
    }

    public FrameLayout initView(Context c,int count,int w,String[] titles) {
        return this.initView(c,count,w,50,titles);
    }

    public FrameLayout initView(Context c,int count,int w,int h,String[] titles) {
        return this.initView(c,count,w,h,titles,null,null);
    }

    public FrameLayout initView(Context c,int count,int width,int height,String[] titles,Drawable bgDrawable,Drawable slideDrawable){
        this.w = dp2px(c,width);
        this.h = dp2px(c,height);
        this.count = count;
        this.singleW = w/4;
        this.currentIndex = 0;

        //初始化titles数组，textView数组,Id数组
        this.titles = titles;
        this.tabs = new TextView[count];
        this.tabsId = new int[count];

        frameLayout = new FrameLayout(c);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(w,h));
        frameLayout.setBackgroundColor(bgColor);
        if(bgDrawable!=null){
            frameLayout.setBackground(bgDrawable);
        }

        //如果数量小于或等0，直接返回
        if(count<=0){
            return frameLayout;
        }

        slider = new View(c);
        FrameLayout.LayoutParams slideParam = new FrameLayout.LayoutParams(singleW-paddingSpace,
                h-paddingSpace);
        slideParam.topMargin = paddingSpace/2;
        slideParam.leftMargin = paddingSpace/2;
        slider.setLayoutParams(slideParam);
        slider.setBackgroundColor(sliderColor);
        if(slideDrawable!=null){
            slider.setBackground(slideDrawable);
        }
        frameLayout.addView(slider);


        for(int i=0;i<count;i++){
            TextView tv = new TextView(c);
            int tvId = View.generateViewId();

            FrameLayout.LayoutParams params =  new FrameLayout.LayoutParams(singleW,h);
            params.leftMargin = i*singleW;
            tv.setLayoutParams(params);
            tv.setTextSize(14);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setGravity(Gravity.CENTER);

            if(i<titles.length){
                String title = titles[i];
                tv.setText(title);
            }else{
                tv.setText("tab"+(i+1));
            }

            if(i == 0){
                tv.setTextColor(textSelectedColor);
            }else {
                tv.setTextColor(textUnselectedColor);
            }
            tv.setId(tvId);

            tv.setOnClickListener(this);
            tabs[i] = tv;
            tabsId[i] = tvId;
            frameLayout.addView(tabs[i]);
        }

        return frameLayout;
    }

    //返回当前下标
    public int getCurrentIndex(){
        return currentIndex;
    }


    //设置整个导航条背景颜色
    public void setBgColor(int color){
        this.bgColor = color;
        if(this.frameLayout!=null) {
            this.frameLayout.setBackgroundColor(bgColor);
        }
    }

    //设置滑块背景drawable
    public void setBgDrawable(Drawable drawable){
        if(this.frameLayout!=null) {
            this.frameLayout.setBackground(drawable);
        }
    }


    //设置滑块背景颜色
    public void setSliderColor(int color){
        this.sliderColor = color;
        if(slider!=null) {
            this.slider.setBackgroundColor(sliderColor);
        }
    }


    //设置滑块背景drawable
    public void setSliderDrawable(Drawable drawable){
        if(slider!=null) {
            this.slider.setBackground(drawable);
        }
    }

    //设置文字大小
    public void setTextSize(int textSize){
        for(int i=0;i<tabs.length;i++){
            tabs[i].setTextSize(textSize);
        }
    }

    //设置选中文字颜色
    public void setTextSelectedColor(int selectedColor){
        this.textSelectedColor = selectedColor;
    }

    //设置未选中文字颜色
    public void setTextUnselectedColor(int unselectedColor){
        this.textUnselectedColor = unselectedColor;
    }


    //滑块内textView点击监听
    @Override
    public void onClick(View v) {
        int checkId = v.getId();
        for(int i = 0;i<tabsId.length;i++){
            if(tabsId[i] == checkId){
                tabs[i].setTextColor(textSelectedColor);
                if(i != currentIndex){
                    if(tabSwitchListener!=null){
                        tabSwitchListener.tabSwitch(tabs[i],i);
                    }
                    playAnimation(slider,currentIndex*singleW+paddingSpace/2,i*singleW);
                    currentIndex = i;
                }
            }else {
                tabs[i].setTextColor(textUnselectedColor);
            }
        }
    }


    //滑块动画效果
    public void playAnimation(View v,float start,float end){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v,"translationX",start,end);
        objectAnimator.setDuration(400);
        objectAnimator.start();
    }


    private int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }


    //下标改变监听回调
    public interface TabSwitchListener{
        public void tabSwitch(TextView tv,int position);
    }
}
