package com.example.sravanthy.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by sandeep on 12/7/2016.
 */
public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {

    private static final String LOG_TAG = NewsLoader.class.getName();

    public NewsLoader(Context context) {

        super(context);
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "onStartLoading()");
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        // Get the list of news items to display
        return QueryUtils.fetchNewsItems();
    }


}
