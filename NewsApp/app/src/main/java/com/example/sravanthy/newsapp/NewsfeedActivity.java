package com.example.sravanthy.newsapp;

import android.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep on 12/7/2016.
 */
public class NewsfeedActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    private NewsAdapter adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressBar loadingIndicator;

    private TextView emptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI(NewsfeedActivity.this);
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupUI(LoaderManager.LoaderCallbacks<? extends Object> args) {
        // Get the ListView
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeTorefresh);
        ListView listView = (ListView) findViewById(R.id.newsfeed_list_view);
        emptyStateView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(emptyStateView);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        final List<NewsItem> newsItems = new ArrayList<>();
        adapter = new NewsAdapter(this, newsItems);

        // Set the adapter to the ListView
        listView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //shuffle();
                mSwipeRefreshLayout.setRefreshing(false);
                if (!isNetworkAvailable()) {
                    Toast.makeText(NewsfeedActivity.this,"no internet connection",Toast.LENGTH_SHORT).show();
                }else if (isNetworkAvailable()) {
                    getLoaderManager().initLoader(0, null, NewsfeedActivity.this);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem currentItem = newsItems.get(position);
                Uri newsItemURI = Uri.parse(currentItem.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, newsItemURI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> data) {
        // When the Loader is done loading, ensure the empty state View reads "No results found."
        emptyStateView.setText(R.string.no_results_found);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of its old data set
        adapter.clear();
        // Update the adapter with new data set
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {

        adapter.clear();
    }
}

