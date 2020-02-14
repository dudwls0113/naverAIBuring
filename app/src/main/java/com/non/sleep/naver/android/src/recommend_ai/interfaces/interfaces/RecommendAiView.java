package com.non.sleep.naver.android.src.recommend_ai.interfaces.interfaces;


import com.non.sleep.naver.android.src.recommend_ai.models.ObjectResponse;

import java.io.InputStream;
import java.util.ArrayList;

public interface RecommendAiView {

    void cpvFailure(String message);

    void cpvSuccess(InputStream inputStream);

    void retrofitFailure(String message);

    void postWordPositiveSuccess();

    void postWordNegativeSuccess();

    void postWordList(ArrayList<ObjectResponse> arrayList);
}
