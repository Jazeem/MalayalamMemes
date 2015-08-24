package com.clusterdev.malayalammemes.malayalammemes;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    private TextView tv;
    private ImageView img;
    private String PostID = null;
    private String PhotoID = null;
    private String baseUrl = "https://graph.facebook.com/v2.4/";
    private String OAuth = "151023885236941|y1tgdIybKDV1JD6etX0AUehPFF0";
    private int counter = 0;
    private JSONArray postArray = null;
    private Context context;
    private LinearLayout linearLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String nextUrl = null;
    private int counter_limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        context = this;
        new RequestPost().execute(baseUrl + "internationalchaluunion/posts");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (PostID != null) {
                    swipeRefreshLayout.setRefreshing(true);
                    Log.d("Swipe", "Refreshing");
                    if (counter < counter_limit) {
                        try {
                            PostID = postArray.getJSONObject(counter).get("id").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new RequestID().execute();
                    }
                    else{
                        counter = 0;
                        new RequestPost().execute(nextUrl);
                    }

                    counter++;
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class RequestPost extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;


            HttpGet httpGet = new HttpGet(uri[0]);
            httpGet.setHeader("Authorization", "OAuth " + OAuth);
            try {
                response = httpclient.execute(httpGet);
                JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
                postArray = (JSONArray) json.get("data");
                counter_limit = postArray.length();
                nextUrl = json.getJSONObject("paging").getString("next").toString();
                Log.v("nextUrl", nextUrl);
                responseString = postArray.getJSONObject(counter).get("id").toString();

                Log.v("id", responseString);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..


            PostID = result;
            counter++;
            new RequestID().execute();

        }
    }

    class RequestID extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {

            String responseString = null;

            if(PostID != null) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String baseUrl = "https://graph.facebook.com/v2.4/";
                HttpGet httpGet;
                httpGet = new HttpGet(baseUrl + PostID + "?fields=object_id");
                httpGet.setHeader("Authorization", "OAuth " + OAuth);
                try {
                    response = httpclient.execute(httpGet);
                    JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    responseString = json.get("object_id").toString();
                    Log.v("object_id", responseString);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
            if(result != null) {
                PhotoID = result;
                new RequestImageURL().execute();
            }
        }
    }

    class RequestImageURL extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            Bitmap bmp = null;
            String baseUrl = "https://graph.facebook.com/v2.4/";
            HttpGet httpGet;
            httpGet = new HttpGet(baseUrl + PhotoID + "/picture");
            httpGet.setHeader("Authorization", "OAuth " + OAuth);
            try {
                response = httpclient.execute(httpGet);
                Log.v("url.raw", response.toString());
                //JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));

//                responseString = json.getJSONObject("data").get("url").toString();
//                Log.v("url", responseString);

                String entityContents = "";
                HttpEntity responseEntity = response.getEntity();
                BufferedHttpEntity httpEntity = null;
                try {
                    httpEntity = new BufferedHttpEntity(responseEntity);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                InputStream imageStream = null;
                try {
                    imageStream = httpEntity.getContent();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                bmp = BitmapFactory.decodeStream(imageStream);


            } catch (IOException e) {
                e.printStackTrace();
            }


            return bmp;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //Do anything with response..

            //ImageView Setup
            DynamicImageView imageView = new DynamicImageView(context, null);
            //setting image resource
            imageView.setImageBitmap(result);
            //setting image position
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 32);
            //imageView.setLayoutParams();
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


            //adding view to layout
            linearLayout.addView(imageView, 0);
            swipeRefreshLayout.setRefreshing(false);



        }
    }
}

