package com.example.sravanthy.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.edttext)
    protected EditText edtSearch;
    @BindView(R.id.btn)
    protected Button btnSearch;
    @BindView(R.id.list)
    protected ListView lvBook;
    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        edtSearch.setText(VariableConstant.EMPTY_STRING);
        edtSearch.setEnabled(true);
        lvBook.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this, "network enabled", Toast.LENGTH_SHORT).show();
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyConnection()) {
                    if (!(edtSearch.getText().toString().isEmpty())) {
                        new DownloadWebpageTask().execute(edtSearch.getText().toString());
                    }
                    else {
                        Toast.makeText(MainActivity.this, "no value", Toast.LENGTH_SHORT).show();
                        lvBook.setVisibility(View.GONE);
                    }
                }

                else{
                    Toast.makeText(MainActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                    edtSearch.setEnabled(true);
                    lvBook.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public boolean verifyConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream with = null;
        try {
            String encodedString = URLEncoder.encode(myurl.toLowerCase(),"UTF-8");
                System.out.format("'%s'\n", encodedString);
            URL url = new URL(VariableConstant.URL_API + encodedString + VariableConstant.URL_API_MAX_RESULTS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(2000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            if (response == 200) {
                with = conn.getInputStream();
                return readIt(with);
            } else {
               return VariableConstant.EMPTY_STRING;
            }
        } finally {
            if (with != null) {
                with.close();

            }
        }
    }


    public String readIt(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString();
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return getApplicationContext().getString(R.string.error_unable_retrieve);
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject reader = new JSONObject(result);
                if (reader.length() > 0) {
                    final List<Book> books = new ArrayList<>();
                    JSONArray items = reader.optJSONArray("items");
                    if(items!=null) {
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            String id = item.optString("id");
                            String link = item.optJSONObject("volumeInfo").optString("infoLink");
                            String image = item.optJSONObject("volumeInfo").optJSONObject("imageLinks").optString("smallThumbnail");
                            String title = item.optJSONObject("volumeInfo").optString("title");
                            String author = item.optJSONObject("volumeInfo").optString("authors");
                            String description = item.optJSONObject("volumeInfo").optString("description");
                            Book book = new Book(id, link, image, title, author, description);
                            books.add(book);
                        }
                    }
                    else{
                        lvBook.setEmptyView(findViewById(R.id.empty_list_item));
                        edtSearch.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,"NO BOOKS ARE AVAIABLE WITH THIS NAME,PLEASE ENTER VALID BOOK NAME",Toast.LENGTH_LONG).show();
                        lvBook.setVisibility(View.GONE);
                        btnSearch.setVisibility(View.VISIBLE);
                    }
                    if (!books.isEmpty()) {
                        BookAdapter adapter = new BookAdapter(getApplicationContext(), R.layout.books_list_items, books);
                        lvBook.setVisibility(View.VISIBLE);
                        lvBook=(ListView)findViewById(R.id.list);
                        lvBook.setAdapter(adapter);
                        lvBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String url = books.get(i).getLink();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                startActivity(intent);
                            }
                        });
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
