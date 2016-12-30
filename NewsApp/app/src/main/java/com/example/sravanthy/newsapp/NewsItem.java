package com.example.sravanthy.newsapp;

import android.graphics.Bitmap;

/**
 * Created by sandeep on 12/7/2016.
 */
public class NewsItem {

    private String url;

    private String webTitle;

    private Bitmap thumbnail;

    private String sectionName;

    private String time;

    private String contributor;

    public NewsItem(String url, String webTitle, Bitmap thumbnail, String sectionName, String time, String contributor) {
        this.url = url;
        this.webTitle = webTitle;
        this.thumbnail = thumbnail;
        this.sectionName = sectionName;
        this.time = time;
        this.contributor = contributor;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getTime() {
        return time;
    }

    public String getContributor() {
        return contributor;
    }
}
