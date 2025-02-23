package com.example.maciagamecenter;

public class Banner {
    private String title;
    private int imageResource;

    public Banner(String title, int imageResource) {
        this.title = title;
        this.imageResource = imageResource;
    }

    public String getTitle() { return title; }
    public int getImageResource() { return imageResource; }
}