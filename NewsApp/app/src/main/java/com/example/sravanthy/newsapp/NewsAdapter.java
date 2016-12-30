package com.example.sravanthy.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by sandeep on 12/7/2016.
 */
public class NewsAdapter extends ArrayAdapter<NewsItem> {
    private static final String LOG_TAG = NewsAdapter.class.getName();
    private static final String BY_ = "by ";
    public NewsAdapter(Context context, List<NewsItem> newsItems) {
        super(context, 0, newsItems);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_items, parent, false);
        }

        // Get the current NewsItem
        NewsItem currentNewsItem = getItem(position);

        // Update the title TextView for the current NewsItem
        updateTitleView(listItemView, currentNewsItem);

        // Update the section TextView for the current NewsItem
        updateSectionView(listItemView, currentNewsItem);

        // Update the web publication date and time TextView for the current NewsItem
        updateDateTimeView(listItemView, currentNewsItem);

        // Update the contributor TextView for the current NewsItem
        updateContributorView(listItemView, currentNewsItem);

        // Update the thumbnail ImageView for the current NewsItem
        updateThumbnailView(listItemView, currentNewsItem);

        return listItemView;
    }

    private void updateThumbnailView(View listItemView, NewsItem currentNewsItem) {
        ImageView thumbnail = (ImageView)
                listItemView.findViewById(R.id.newsfeed_list_item_image);

        if (currentNewsItem.getThumbnail() != null) {
            thumbnail.setImageBitmap(currentNewsItem.getThumbnail());
        } else {
            thumbnail.setImageResource(R.drawable.null_thumbnail);
        }
    }

    private void updateContributorView(View listItemView, NewsItem currentNewsItem) {
        // Update the contributor name for the NewsItem
        TextView contributorView = (TextView)
                listItemView.findViewById(R.id.newsfeed_list_item_contributor);
        String contributorName = currentNewsItem.getContributor();
        if (contributorName != "") {
            contributorName = BY_ + contributorName;
        }
        contributorView.setText(contributorName);
    }

    private void updateDateTimeView(View listItemView, NewsItem currentNewsItem) {
        // Update the web publication date TextView for the NewsItem
        TextView dateView = (TextView)
                listItemView.findViewById(R.id.newsfeed_list_item_date);
        String formattedTime = formatDate(currentNewsItem.getTime());
        dateView.setText(formattedTime);
    }

    private void updateSectionView(View listItemView, NewsItem currentNewsItem) {
        // Update the section TextView for the NewsItem
        TextView sectionView = (TextView)
                listItemView.findViewById(R.id.newsfeed_list_item_section);
        sectionView.setText(currentNewsItem.getSectionName().toUpperCase());
    }

    private void updateTitleView(View listItemView, NewsItem currentNewsItem) {
        // Update the title TextView for the NewsItem
        TextView titleView = (TextView)
                listItemView.findViewById(R.id.newsfeed_list_item_title);
        titleView.setText(currentNewsItem.getWebTitle());
    }

    private String formatDate(String datetime) {
        SimpleDateFormat expectedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        // The datetime to be returned after formatting
        String formattedDateTime = "";
        Log.i(LOG_TAG, datetime);

        try {
            Date dateAndTime = expectedFormat.parse(datetime);
            SimpleDateFormat desiredFormat = new SimpleDateFormat("EEE, d MMM yyyy 'at' hh:mm aaa");
            formattedDateTime = desiredFormat.format(dateAndTime);

        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing date and time", e);
        }

        return formattedDateTime;
    }
}
