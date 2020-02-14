package com.non.sleep.naver.android.src.recommend_yes;

import android.util.Log;

import com.non.sleep.naver.android.src.recommend.interfaces.RecommendRetrofitInterface;
import com.non.sleep.naver.android.src.recommend_yes.interfaces.RecommendYesRetrofitInterface;
import com.non.sleep.naver.android.src.recommend_yes.interfaces.RecommendYesView;
import com.non.sleep.naver.android.src.recommend_yes.models.RecommendResponse;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.non.sleep.naver.android.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.non.sleep.naver.android.src.ApplicationClass.getRetrofit;

public class RecommendYesService {

    private final RecommendYesView mRecommendYesView;

    RecommendYesService(RecommendYesView recommendYesView){
        mRecommendYesView = recommendYesView;
    }

    void postRecommend(int age, String gender){
        JSONObject params = new JSONObject();
        try {
            params.put("age", age);
            params.put("gender", gender);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final RecommendYesRetrofitInterface recommendYesRetrofitInterface = getRetrofit().create(RecommendYesRetrofitInterface.class);
        recommendYesRetrofitInterface.postRecommend(RequestBody.create(params.toString(), MEDIA_TYPE_JSON)).enqueue(new Callback<RecommendResponse>() {
            @Override
            public void onResponse(Call<RecommendResponse> call, Response<RecommendResponse> response) {
                final RecommendResponse recommendResponse = response.body();
                if(recommendResponse==null){
                    System.out.println("에러에러");
                    mRecommendYesView.retrofitFailure(null);
                }
                else if(recommendResponse.getCode()==100){
                    mRecommendYesView.postRecommendSuccess(recommendResponse.getRecommendObjects());
                }
                else{
                    mRecommendYesView.retrofitFailure(recommendResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<RecommendResponse> call, Throwable t) {
                mRecommendYesView.retrofitFailure(null);
                Log.d("error", t.toString());
            }
        });
    }
}
