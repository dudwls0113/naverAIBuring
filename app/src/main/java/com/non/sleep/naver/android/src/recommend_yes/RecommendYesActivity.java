package com.non.sleep.naver.android.src.recommend_yes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.non.sleep.naver.android.R;
import com.non.sleep.naver.android.src.BaseActivity;
import com.non.sleep.naver.android.src.recommend_yes.interfaces.RecommendYesView;
import com.non.sleep.naver.android.src.recommend_yes.models.RecommendObject;

import java.util.ArrayList;

public class RecommendYesActivity extends BaseActivity implements RecommendYesView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_yes);
        init();
    }

    void init(){
        int age = getIntent().getIntExtra("age",0);
        String gender = getIntent().getStringExtra("gender");
        postRecommend(age, gender);
    }

    void postRecommend(int age, String gender){
        showProgressDialog();
        final RecommendYesService recommendYesService = new RecommendYesService(this);
        recommendYesService.postRecommend(20, "F");

    }

    @Override
    public void retrofitFailure(String message) {
        hideProgressDialog();
        showCustomToast(message == null || message.isEmpty() ? getString(R.string.network_error) : message);
    }

    @Override
    public void postRecommendSuccess(ArrayList<RecommendObject> arrayList) {
        hideProgressDialog();
        Log.d("name", arrayList.get(0).getName());
    }
}
