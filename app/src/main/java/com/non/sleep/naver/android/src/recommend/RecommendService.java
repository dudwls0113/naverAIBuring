package com.non.sleep.naver.android.src.recommend;

import com.non.sleep.naver.android.src.recommend.interfaces.RecommendView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RecommendService {

    private final RecommendView mRecommendView;

    RecommendService(RecommendView recommendView){
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

}
