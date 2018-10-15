package com.szsszwl.jnipro;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by DeskTop29 on 2018/9/21.
 */

public class ExceptionThrowActivity extends Activity implements View.OnClickListener{

    Button execNative;

    static {
        System.loadLibrary("exceptionThrow");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exception_throw_activity);

        execNative = (Button) findViewById(R.id.exec_native);
        execNative.setOnClickListener(this);
    }

    public native void doit();

    public void arithmeticCal(){
        int a = 0;
        int b = 10/a;
        System.out.println("----> "+b);
    }

    public static void normalCallback() {
        System.out.println("In Java: invoke normalCallback.");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exec_native:
                doit();
                break;
        }
    }
}
