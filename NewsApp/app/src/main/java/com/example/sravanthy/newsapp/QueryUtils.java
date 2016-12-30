package com.example.sravanthy.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep on 12/7/2016.
 */
public class QueryUtils {
    private static String queryUrl =
            "https://content.guardianapis.com/search?q=debate%20AND%20economy&tag=politics/politics&from-date=2016-12-01&api-key=test";   private static final String LOG_TAG = QueryUtils.class.getName();

    private QueryUtils() {
    }

    public static List<NewsItem> fetchNewsItems() {
        List<NewsItem> newsItems;
        URL url = createURL(queryUrl);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
            System.out.println(jsonResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request, fetchNewsItems");
        }

        // Extract relevant fields from the JSON response and return a list of NewsItems
        return extractResultFromJson(jsonResponse);
    }

    private static List<NewsItem> extractResultFromJson(String jsonResponse) {
        // Create an empty list of NewsItems
        List<NewsItem> newsItems = new ArrayList<>();

        // if there's no JSON string to parse, there's no point trying to parse it. Finish early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return newsItems;
        }
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject responseObject = root.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");

            // For each element in the results array, do the following
            for (int i = 0; i < resultsArray.length(); i++) {
                // Get the JSONObject representing a particular item on the Guardian website
                JSONObject result = resultsArray.getJSONObject(i);

                // Get the title, section name, date published, URL for this item
                // on the Guardian website
                String sectionName = result.optString("sectionName");
                String webPublicationDate = result.optString("webPublicationDate");
                String webTitle = result.optString("webTitle");
                String webUrl = result.optString("webUrl");

                // Get the thumbnail (returns null if no thumbnail for this image)
                Bitmap thumbnail = getThumbnail(result);

                // Get the name of the contributor
                String contributor = getContributor(result);

                // Create a new NewsItem from the info parsed, and add it to the list of NewsItems
                newsItems.add(new NewsItem(webUrl, webTitle, thumbnail, sectionName,
                        webPublicationDate, contributor));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON, extractFeatureFromJson", e);
        }

        return newsItems;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        // the string to be returned from the server after the HTTP request is made
        String jsonResponse = "";
        // The HTTP client which will act as a communications link between the application and a URL
        HttpURLConnection connection = null;
        // The stream that we will receive the data over if successful
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + responseCode);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG,
                    "Error retrieving JSON response. Check internet connection?, makeHttpRequest");
        } finally {
            // close resources
            if (connection != null) {
                connection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        // if there is an InputStream, create a BufferedReader to read from it into a StringBuilder
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                Log.i(LOG_TAG, line);
                output.append(line);
                line = reader.readLine();
            }
            inputStreamReader.close();
            reader.close();
        }

        return output.toString();
    }

    private static URL createURL(String stringURL) {
        URL url = null;

        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem in URL, createURL");
            return null;
        }

        return url;
    }

    private static Bitmap getBitmapFromURL(String imageURL) {
        try {
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
    private static Bitmap getThumbnail(JSONObject result) throws JSONException {
        // Get the JSONObject with the key "fields" for the particular result being parsed
        JSONObject fieldsObject = result.optJSONObject("fields");
        Bitmap thumbnail = null;
        if (fieldsObject != null) {
            String thumbnailUrlString = fieldsObject.getString("thumbnail");
            thumbnail = getBitmapFromURL(thumbnailUrlString);
        }

        return thumbnail;
    }
    private static String getContributor(JSONObject result) throws JSONException {
        String contributor = "";
        JSONArray tagsArray = result.optJSONArray("tags");

        // If there is an array for tags, get the JSONObject for the contributor's tags
        if (tagsArray != null) {
            JSONObject contributorTags = tagsArray.optJSONObject(0);

            // if there is a section for the contributor's tags, get the contributor's name
            if (contributorTags != null) {
                contributor = contributorTags.getString("webTitle");
            }
        }
        return contributor;
    }
}
