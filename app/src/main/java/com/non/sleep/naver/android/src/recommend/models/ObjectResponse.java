package com.non.sleep.naver.android.src.recommend.models;

import com.google.gson.annotations.SerializedName;

public class ObjectResponse {
    @SerializedName("menuNo")
    int menuNo;
    @SerializedName("name")
    String name;
    @SerializedName("imageUrl")
    String imageUrl;
    @SerializedName("content")
    String content;
    @SerializedName("price")
    int price;
    @SerializedName("category")
    String category;

    public int getMenuNo() {
        return menuNo;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getContent() {
        return content;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }
}
