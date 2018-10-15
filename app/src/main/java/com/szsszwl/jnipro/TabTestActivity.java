package com.szsszwl.jnipro;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * Created by DeskTop29 on 2018/8/21.
 */

public class TabTestActivity extends TabActivity {
    TabHost tabHost;
    FrameLayout tabContent;
    TabWidget tabWidget;

    LinearLayout tabContent1,tabContent2,tabContent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view);

        tabHost = getTabHost();
        tabWidget = getTabWidget();

        tabContent1 = (LinearLayout) findViewById(R.id.tab1);
        tabContent2 = (LinearLayout) findViewById(R.id.tab2);
        tabContent3 = (LinearLayout) findViewById(R.id.tab3);

        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("tab1");
        tabSpec1.setContent(R.id.tab1);
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("tab2");
        tabSpec2.setContent(R.id.tab2);
        TabHost.TabSpec tabSpec3 = tabHost.newTabSpec("tab3");
        tabSpec3.setIndicator("tab3");
        tabSpec3.setContent(R.id.tab3);

        //View v = getLayoutInflater().inflate(R.layout.item,tabWidget,false);
        //tabWidget.addView(v,3);

        tabHost.addTab(tabSpec1);
        tabHost.addTab(tabSpec2);
        tabHost.addTab(tabSpec3);

        tabHost.setCurrentTab(0);
    }


}
