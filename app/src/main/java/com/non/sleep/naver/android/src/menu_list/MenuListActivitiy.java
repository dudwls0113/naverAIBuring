package com.non.sleep.naver.android.src.menu_list;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.non.sleep.naver.android.R;
import com.non.sleep.naver.android.src.BaseActivity;

public class MenuListActivitiy extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list_activitiy);

    }

    public void customOnClick(View view) {
        switch (view.getId()){
            case R.id.activity_main_iv_recording:

        }
    }
}
