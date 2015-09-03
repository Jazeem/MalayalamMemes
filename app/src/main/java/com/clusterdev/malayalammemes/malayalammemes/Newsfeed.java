package com.clusterdev.malayalammemes.malayalammemes;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shahas on 8/28/15.
 */
public class Newsfeed extends Fragment {

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

    private RefreshableFragmentActivity activity; //so that we can update the view when changing sharedpreferences



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //LinearLayout        llLayout    = (LinearLayout)    inflater.inflate(R.layout.fragment, container, false);
        View view = inflater.inflate(R.layout.newsfeed, container, false);

        activity = (RefreshableFragmentActivity)getActivity();

        swipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
        context = getActivity();
        new RequestPost().execute(baseUrl + "internationalchaluunion/posts");
        swipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
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
                    } else {
                        counter = 0;
                        new RequestPost().execute(nextUrl);
                    }

                    counter++;
                }
            }
        });

        return view;
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
                    if(json.has("object_id"))
                        responseString = json.get("object_id").toString();
                    //Log.v("object_id", responseString);
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
            else
                swipeRefreshLayout.setRefreshing(false);
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

            int maxImageSize = 500;
            //bmp = Bitmap.createScaledBitmap(bmp, 500, 500, false);
            float ratio = Math.min(
                    (float) maxImageSize / bmp.getWidth(),
                    (float) maxImageSize / bmp.getHeight());
            int width = Math.round((float) ratio * bmp.getWidth());
            int height = Math.round((float) ratio * bmp.getHeight());

            Bitmap newBitmap = Bitmap.createScaledBitmap(bmp, width,
                    height, true);
            return newBitmap;

        }


        @Override
        protected void onPostExecute(final Bitmap result) {
            super.onPostExecute(result);
            //Do anything with response..



            LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View cardView = vi.inflate(R.layout.troll_card, null);
            cardView.setTag(R.string.tag_photo_id, PhotoID);
            cardView.setTag(R.string.tag_clicked, false);

            //ImageView Setup
            DynamicImageView imageView = (DynamicImageView) cardView.findViewById(R.id.dynamic_image_view);
            //setting image resource
            imageView.setImageBitmap(result);
            //setting image position

            //imageView.setLayoutParams();
            //adding view to layout
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MainActivity.this, PhotoViewer.class);
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    result.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] bytes = stream.toByteArray();
//                    intent.putExtra("BMP",bytes);
//
//                    startActivity(intent);
//                }
//            });

            ImageView share = (ImageView) cardView.findViewById(R.id.whatsapp_share);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    result.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                    try {
                        f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                    share.putExtra(Intent.EXTRA_TEXT, "Awesome troll send through awesome app made by awesome developer");
                    share.setPackage("com.whatsapp");
                    startActivityForResult(Intent.createChooser(share, "Share!"), 0);
                }
            });

            final ImageView favourite = (ImageView) cardView.findViewById(R.id.favourite_button);
            favourite.setOnClickListener(new View.OnClickListener(){
                @TargetApi(Build.VERSION_CODES.KITKAT)
                public void onClick(View v) {
                    SharedPreferences favourites = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = favourites.edit();
                    String jsonString = favourites.getString("BYTE_ARRAY", "");
                    JSONObject jsonObject = new JSONObject();
                    if (cardView.getTag(R.string.tag_clicked).equals(false)) {


                        JSONObject imageJSON = new JSONObject();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bytes = stream.toByteArray();
                        String byteString = Base64.encodeToString(bytes, Base64.DEFAULT);
                        try {
                            imageJSON.put("photoID", cardView.getTag(R.string.tag_photo_id));
                            imageJSON.put("byteArrayString", byteString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }





                        if (jsonString.equals("")) {

                            JSONArray imageJSONS = new JSONArray();

                            imageJSONS.put(imageJSON);
                            try {
                                jsonObject.put("data", imageJSONS);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.v("test", "image added to sharedpref");


                        } else {


                            try {
                                jsonObject = new JSONObject(jsonString);
                                //Log.v("Array size before", ""+jsonObject.getJSONArray("data").length());
                                //Log.v("before adding",jsonObject.getJSONArray("data").toString());
                                jsonObject.getJSONArray("data").put(imageJSON);

                                //Log.v("after adding",jsonObject.getJSONArray("data").toString());
                                //  Log.v("Array size after", ""+jsonObject.getJSONArray("data").length());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        editor.putString("BYTE_ARRAY", jsonObject.toString());
                        editor.commit();

                        favourite.setImageResource(R.drawable.like);

                        cardView.setTag(R.string.tag_clicked, true);
                    }
                    else {
                        try {
                            jsonObject = new JSONObject(jsonString);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            int i;
                            Log.v("card photo id", cardView.getTag(R.string.tag_photo_id).toString());
                            for (i=0; i<jsonArray.length(); i++){
                                Log.v("JSON"+i,jsonArray.getJSONObject(i).get("photoID").toString());
                                if (jsonArray.getJSONObject(i).get("photoID").equals(cardView.getTag(R.string.tag_photo_id)))
                                    break;
                            }
                            if(i != jsonArray.length())
                                jsonArray.remove(i);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cardView.setTag(R.string.tag_clicked, false);
                        favourite.setImageResource(R.drawable.like_grey);



                        editor.putString("BYTE_ARRAY", jsonObject.toString());
                        editor.commit();

                    }
                    activity.refreshFavourites();
                }

            });

            linearLayout.addView(cardView);
            swipeRefreshLayout.setRefreshing(false);



        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("check", "delete");
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        f.delete();
    }
}