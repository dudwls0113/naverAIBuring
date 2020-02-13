package com.non.sleep.naver.android.src.recommend.interfaces;

import java.io.InputStream;

public interface RecommendView {

    void cpvFailure(String message);

    void cpvSuccess(InputStream inputStream);

}
