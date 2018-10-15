package com.szsszwl.jnipro;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by DeskTop29 on 2018/9/21.
 */

public class LocalRefOverflowActivity extends Activity implements View.OnClickListener{
    EditText count;
    Button testBtn;
    TextView logInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_ref_overflow_activity);

        count = (EditText) findViewById(R.id.count);
        testBtn = (Button) findViewById(R.id.test_btn);
        logInfo = (TextView) findViewById(R.id.log_info);

        testBtn.setOnClickListener(this);

    }

    // 返回count个sample相同的字符串数组，并用编号标识，如：sample1，sample2...
    public native String[] getStrings(int count, String sample);


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.test_btn:
                String testNum = count.getText().toString();
                int num = TextUtils.isEmpty(testNum)?0:Integer.valueOf(testNum);
                String[] strings = getStrings(num,"I Love You %d Year！！！");
                StringBuilder sb = new StringBuilder();

                Log.i("jni","return back string array size : "+strings.length);
                for (String string : strings) {
                    sb.append(string+"\n");
                    Log.i("jni",string);
                }
                logInfo.setText(sb.toString());
                break;
        }
    }

    static {
        System.loadLibrary("overflow");
    }
}
