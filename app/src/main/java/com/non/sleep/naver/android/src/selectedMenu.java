package com.non.sleep.naver.android.src;

public class selectedMenu {
    String name;
    int price;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return 1000;
    }

    public selectedMenu(String name, int price) {
        this.name = name;
        this.price = price;
    }


}
