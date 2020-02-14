package com.non.sleep.naver.android.src.shopping;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.non.sleep.naver.android.R;

public class ShoppingActivity extends AppCompatActivity {
    private TextView mTvName1, mTvName2, mTvName3, mTvWon1, mTvWon2, mTvWon3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        String name1 = getIntent().getStringExtra("name1");
        String name2 = getIntent().getStringExtra("name2");
        String name3 = getIntent().getStringExtra("name3");

        int won1 = getIntent().getIntExtra("won1",0);
        int won2 = getIntent().getIntExtra("won2",0);
        int won3 = getIntent().getIntExtra("won3",0);

        if(name1 != null && name1!="") {
            mTvName1.setText(name1);
        }
        if(name2 != null && name2 !="") {
            mTvName2.setText(name2);
        }
        if(name3 != null && name3 !="") {
            mTvName3.setText(name3);
        }

        mTvWon1.setText(won1 + "원");
        mTvWon2.setText(won2 + "원");
        mTvWon3.setText(won3 + "원");
    }

    public void customOnClick(View view) {
        switch (view.getId()) {
            case R.id.shopping_yes_iv:
                break;
            case R.id.shopping_no_iv:
                break;
        }
    }
}
