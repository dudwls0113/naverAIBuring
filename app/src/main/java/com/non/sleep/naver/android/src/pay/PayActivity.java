package com.non.sleep.naver.android.src.pay;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.non.sleep.naver.android.R;

import static com.non.sleep.naver.android.src.ApplicationClass.arrayListSelectedMenu;

public class PayActivity extends AppCompatActivity {

    private LinearLayout mLinearMenu1, mLinearMenu2, mLinearMenu3;
    private TextView mTextViewMenu1, mTextViewMenu2, mTextViewMenu3, mTextViewTotal;
    int price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        init();
    }


    void init(){
        mLinearMenu1 = findViewById(R.id.linear_menu_1);
        mLinearMenu2 = findViewById(R.id.linear_menu_2);
        mLinearMenu3 = findViewById(R.id.linear_menu_3);

        mTextViewMenu1 = findViewById(R.id.pay_name1_tv);
        mTextViewMenu2 = findViewById(R.id.pay_name2_tv);
        mTextViewMenu3 = findViewById(R.id.pay_name3_tv);
        mTextViewTotal = findViewById(R.id.total_won_tv);

        for (int i = 0; i < arrayListSelectedMenu.size(); i++) {
            if (i == 0) {
                mLinearMenu1.setVisibility(View.VISIBLE);
                mTextViewMenu1.setText(arrayListSelectedMenu.get(i).getName());
//                mTextViewMenu1.setText(arrayListSelectedMenu.get(i).getPrice() + "원");
            } else if (i == 1) {
                mLinearMenu2.setVisibility(View.VISIBLE);
                mTextViewMenu2.setText(arrayListSelectedMenu.get(i).getName());
//                mTextViewMenu2.setText(arrayListSelectedMenu.get(i).getPrice() + "원");
            } else {
                mLinearMenu3.setVisibility(View.VISIBLE);
                mTextViewMenu3.setText(arrayListSelectedMenu.get(i).getName());
//                mTextViewMenu2.setText(arrayListSelectedMenu.get(i).getPrice() + "원");
            }

            price+=arrayListSelectedMenu.get(i).getPrice();

        }

        mTextViewTotal.setText(price+"원");

    }
}
