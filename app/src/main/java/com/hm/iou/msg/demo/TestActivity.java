package com.hm.iou.msg.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hm.iou.msg.business.message.view.MsgCenterFragment;

/**
 * Created by hjy on 2019/2/20.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.ll_content, new MsgCenterFragment())
                .commit();
    }
}
