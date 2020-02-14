package com.non.sleep.naver.android.src.recommend_ai;

import android.util.Log;

import com.non.sleep.naver.android.src.recommend.interfaces.RecommendRetrofitInterface;
import com.non.sleep.naver.android.src.recommend_ai.models.WordResponse;
import com.non.sleep.naver.android.src.recommend_ai.interfaces.interfaces.RecommendAiRetrofitInterface;
import com.non.sleep.naver.android.src.recommend_ai.interfaces.interfaces.RecommendAiView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.non.sleep.naver.android.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.non.sleep.naver.android.src.ApplicationClass.getRetrofit;

public class RecommendAiService {

    private final RecommendAiView mRecommendView;

    RecommendAiService(RecommendAiView recommendView){
        mRecommendView = recommendView;
    }

    void postCPV(String input){
        String clientId = "g0fd605ajk";
        String clientSecret = "ZgiGkHGhY3kNc5ulmYD70rkKAM3FeGnONBZpjN63";
        try {
            String text = URLEncoder.encode(input, "UTF-8"); // 13자
            String apiURL = "https://naveropenapi.apigw.ntruss.com/voice-premium/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            // post request
            String postParams = "speaker=nara&volume=0&speed=0&pitch=0&emotion=0&format=mp3&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                System.out.println("성공");
                InputStream is = con.getInputStream();
                mRecommendView.cpvSuccess(is);
                System.out.println("성공2");
            } else {  // 오류 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                mRecommendView.cpvFailure(response.toString());
                System.out.println("리스폰스 에러: " + response.toString());
            }
        } catch (Exception e) {
            mRecommendView.cpvFailure(null);
            System.out.println("error: " + e);
        }
    }

    void postWord(String word) {
        JSONObject params = new JSONObject();
        try {
            params.put("word",word);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final RecommendAiRetrofitInterface recommendRetrofitInterface = getRetrofit().create(RecommendAiRetrofitInterface.class);
        recommendRetrofitInterface.postWord(RequestBody.create(params.toString(),MEDIA_TYPE_JSON)).enqueue(new Callback<WordResponse>() {
            @Override
            public void onResponse(Call<WordResponse> call, Response<WordResponse> response) {
                final WordResponse wordResponse = response.body();
                if(wordResponse==null){
                    mRecommendView.retrofitFailure(null);
                }
                else if(wordResponse.getCode()==1){
                    mRecommendView.postWordPositiveSuccess();
                }
                else if(wordResponse.getCode()==2){
                    mRecommendView.postWordNegativeSuccess();
                }
                else{
                    mRecommendView.retrofitFailure(wordResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<WordResponse> call, Throwable t) {
                mRecommendView.retrofitFailure(null);
            }
        });
    }

    void postWordList(int age, String gender) {
        JSONObject params = new JSONObject();
        try {
            params.put("age",age);
            params.put("gender",gender);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final RecommendAiRetrofitInterface recommendAiRetrofitInterface = getRetrofit().create(RecommendAiRetrofitInterface.class);
        recommendAiRetrofitInterface.postWordList(RequestBody.create(params.toString(),MEDIA_TYPE_JSON)).enqueue(new Callback<WordResponse>() {
            @Override
            public void onResponse(Call<WordResponse> call, Response<WordResponse> response) {
                Log.i("SVDS", "SDV");
                final WordResponse wordResponse = response.body();
                Log.i("SDVsdaa", wordResponse.getMessage());
                Log.i("SDVsdaa", String.valueOf(wordResponse.getObjectResponses().size()));
                if(wordResponse==null){
                    mRecommendView.retrofitFailure(null);
                }
                else if(wordResponse.getCode()==1){
                    mRecommendView.postWordPositiveSuccess();
                }
                else if(wordResponse.getCode()==2){
                    mRecommendView.postWordNegativeSuccess();
                }
                else if(wordResponse.getCode() == 100) {
                    Log.i("Vsdavds", String.valueOf(wordResponse.getObjectResponses().size()));
                    mRecommendView.postWordList(wordResponse.getObjectResponses());
                }
                else{
                    mRecommendView.retrofitFailure(wordResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<WordResponse> call, Throwable t) {
                mRecommendView.retrofitFailure(null);
            }
        });
    }

}
