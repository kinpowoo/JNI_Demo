package com.szsszwl.jnipro.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.szsszwl.jnipro.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DeskTop29 on 2018/5/16.
 */

public class HorizontalAutoScrollLayout extends ViewGroup {

    int rowCount;
    float horizontalMargin;

    LayoutInflater layoutInflater;

    MyBaseAdapter adapter;
    List<View> viewArray;        //一个先进先出的视图队列

    OnItemChangeListener itemChangeListener;   //currentIndex改变后的回调
    int currentIndex;           //当前view的在数据集中的位置
    boolean keepPlaying;        //是否继续执行动画
    long duration;              //动画执行时间周期


    public void setOnItemChangeListener(OnItemChangeListener itemChangeListener) {
        this.itemChangeListener = itemChangeListener;
    }


    public HorizontalAutoScrollLayout(Context context) {
        this(context, null);
    }

    public HorizontalAutoScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalAutoScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HorizontalAutoScrollLayout);

        rowCount = mTypedArray.getInt(R.styleable.HorizontalAutoScrollLayout_rowCount, 3);
        horizontalMargin = mTypedArray.getDimension(R.styleable.HorizontalAutoScrollLayout_horizontalMargin, 10);

        duration = 3000;    //默认3秒执行一次
        layoutInflater = LayoutInflater.from(context);

        mTypedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int layoutWidth = 0;
        int layoutHeight = 0;
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int cWidth = 0;
        int cHeight = 0;
        int count = getChildCount();

        if (widthMode == MeasureSpec.EXACTLY) {
            //如果布局容器的宽度模式是确定的（具体的size或者match_parent），直接使用父窗体建议的宽度
            layoutWidth = sizeWidth;

        } else {     // WRAP_CONTENT

            //如果是未指定或者wrap_content，我们都按照包裹内容做，宽度方向上只需要拿到所有子控件中宽度做大的作为布局宽度
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                cWidth = child.getMeasuredWidth();
                //获取子控件最大宽度
                layoutWidth += cWidth;
                if (layoutWidth > sizeWidth) {
                    layoutWidth = sizeWidth;
                    break;
                }
            }
        }
        //高度选择高度最大的那一个
        if (heightMode == MeasureSpec.EXACTLY) {
            layoutHeight = sizeHeight;
        } else {       // WRAP_CONTENT
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                cHeight = child.getMeasuredHeight();
                layoutHeight = cHeight > layoutHeight ? cHeight : layoutHeight;
            }
        }

        // 测量并保存layout的宽高
        layoutWidth += (getPaddingLeft() + getPaddingRight() + (rowCount - 1) * horizontalMargin);
        layoutHeight += (getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(layoutWidth, layoutHeight);
    }


    //获取行数
    public int getRowCount() {
        return rowCount;
    }

    //获取item之间的宽度
    public float getHorizontalMargin() {
        return horizontalMargin;
    }

    //设置动画执行周期
    public void setAnimationDuration(long time){
        this.duration = time;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childMeasureWidth = 0;
        int childMeasureHeight = 0;
        int layoutWidth = getPaddingLeft();    // 容器已经占据的宽度
        int layoutHeight = getPaddingTop();   // 容器已经占据的宽度


        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            //注意此处不能使用getWidth和getHeight，这两个方法必须在onLayout执行完，才能正确获取宽高
            childMeasureWidth = child.getMeasuredWidth();
            childMeasureHeight = child.getMeasuredHeight();


            if (layoutWidth < getWidth()) {
                //如果一行没有排满，继续往右排列
                left = layoutWidth;
                right = left + childMeasureWidth;
                top = layoutHeight;
                bottom = top + childMeasureHeight;
            }
            if (i != count - 1) {
                layoutWidth += (childMeasureWidth + horizontalMargin);  //宽度累加
            } else {
                layoutWidth += childMeasureWidth;  //宽度累加
            }

            //确定子控件的位置，四个参数分别代表（左上右下）点的坐标值
            child.layout(left, top, right, bottom);
        }
    }



    //设置数据源
    public void setAdapter(MyBaseAdapter adapter) {
        this.adapter = adapter;
        insertView();

        //监听adapter的数据更新
        this.adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                //先停止动画，避免View null指针
                if(keepPlaying == true) {
                    switchPlayFlag();
                }

                //插入view
                insertView();
            }
        });
    }


    //动态改变显示个数
    public void setRowCount(int rowCountNew){
        if(rowCountNew == rowCount){
            return;
        }

        if(rowCountNew<3){
            rowCount = 3;
        }else {
            rowCount = rowCountNew;
        }

        adapter.setRowCount(rowCount);
        adapter.notifyDataSetChanged();
    }


    private void insertView() {
        //在装载视图前先将循环停止,重置参数
        removeAllViews();
        currentIndex = 2;
        keepPlaying = true;

        int extraViewCount = rowCount - 1;
        viewArray = new ArrayList<>(adapter.getCount() + extraViewCount);



        if (adapter.getCount() > rowCount) {
            for (int i = 0; i < adapter.getCount() + extraViewCount; i++) {
                if (i >= adapter.getCount() && i < adapter.getCount() + extraViewCount) {
                    int realIndex = i - adapter.getCount();
                    viewArray.add(i, viewArray.get(realIndex));
                } else if (i < adapter.getCount()) {
                    View item = adapter.getView(i, null, this);
                    item.setTag(i);
                    viewArray.add(i, item);
                }

            }
        } else {
            for (int i = 0; i < adapter.getCount(); i++) {
                View item = adapter.getView(i, null, this);
                item.setTag(i);
                viewArray.set(i, item);
            }
        }


        //如果子view的个数大于 容器一行显示的数量，可以进行滚动动画
        if (adapter.getCount() > rowCount) {
            scrollCircle();
        }
    }




    //开始滚动循环
    private void scrollCircle() {
        for (int i = 0; i < rowCount +1 ; i++) {
            View item = viewArray.get(i);
            addView(item);
        }
        execAnim();
    }


    private void execAnim() {
        if (getChildCount() <= 0 || !keepPlaying) {
            return;
        }


        final int count = viewArray.size();
        if (currentIndex + rowCount-1 > count-1) {
            currentIndex = 0;
        }

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, adapter.getCellWidth() + horizontalMargin);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                for (int i =0; i < getChildCount(); i++) {
                    View v = getChildAt(i);
                    v.setTranslationX(-value);
                }
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                View v = getChildAt(0);
                if(itemChangeListener!=null){
                    itemChangeListener.onItemChange(v, (int) v.getTag());
                }
                detachViewFromParent(v);
                addView(viewArray.get(currentIndex+rowCount-1));
                currentIndex++;
                execAnim();
            }
        });
        valueAnimator.start();
    }






    //动画加入移除动画，暂时没用到
    private void groupLayoutAnimation() {
        LayoutTransition mLayoutTransition = new LayoutTransition();

        //设置每个动画持续的时间
        mLayoutTransition.setStagger(LayoutTransition.CHANGE_APPEARING, 50);
        mLayoutTransition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 50);
        mLayoutTransition.setStagger(LayoutTransition.APPEARING, 500);
        mLayoutTransition.setStagger(LayoutTransition.DISAPPEARING, 50);

        PropertyValuesHolder appearingScaleX = PropertyValuesHolder.ofFloat("scaleX", 0f, 1.0f);
        PropertyValuesHolder appearingAlpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        ObjectAnimator mAnimatorAppearing = ObjectAnimator.ofPropertyValuesHolder(this, appearingAlpha, appearingScaleX);
        //为LayoutTransition设置View加入动画
        mLayoutTransition.setAnimator(LayoutTransition.APPEARING, mAnimatorAppearing);


        PropertyValuesHolder disappearingAlpha = PropertyValuesHolder.ofFloat("translationX", getPaddingLeft(), -((MyAdapter) adapter).getCellWidth());
        PropertyValuesHolder disappearingRotationY = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
        ObjectAnimator mAnimatorDisappearing = ObjectAnimator.ofPropertyValuesHolder(this, disappearingAlpha, disappearingRotationY);
        //为LayoutTransition设置消失View的动画
        mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, mAnimatorDisappearing);


        ObjectAnimator mAnimatorChangeDisappearing = ObjectAnimator.ofFloat(null, "scaleX", 0.5f, 1f);
        //为LayoutTransition设置View消失时其它View的动画
        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, mAnimatorChangeDisappearing);

        ObjectAnimator mAnimatorChangeAppearing = ObjectAnimator.ofFloat(null, "scaleX", 0.5f, 1f);
        //为为LayoutTransition设置View加入时其它View的动画
        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, mAnimatorChangeAppearing);

        //为ViewGroup设置mLayoutTransition对象
        this.setLayoutTransition(mLayoutTransition);
    }



    //设置动画播放或停止
    public void switchPlayFlag(){
        boolean oldFlag = keepPlaying;
        keepPlaying = !keepPlaying;
        if(oldFlag == false){
            execAnim();
        }
    }


    public interface OnItemChangeListener {
        public void onItemChange(View v,int pos);
    }


}
