package com.clusterdev.malayalammemes.malayalammemes;

import android.annotation.TargetApi;
import android.app.Activity;
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

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

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


public class MainActivity extends Activity {

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
    private SwipyRefreshLayout swipeRefreshLayout;
    private String nextUrl = null;
    private int counterLimit;
    private int imageCount = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        context = this;
        new RequestPost().execute(baseUrl + "internationalchaluunion/posts");
        swipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                if (PostID != null) {
                    swipeRefreshLayout.setRefreshing(true);
                    Log.d("Swipe", "Refreshing");
                    if (counter < counterLimit) {
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
                counterLimit = postArray.length();
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
            linearLayout.addView(imageView);
            swipeRefreshLayout.setRefreshing(false);



        }
    }
}

