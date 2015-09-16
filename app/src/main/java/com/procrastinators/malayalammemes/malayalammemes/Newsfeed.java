package com.procrastinators.malayalammemes.malayalammemes;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String postUrl = null;
    private String baseUrl = "https://graph.facebook.com/v2.4/";
    private String OAuth = "151023885236941|y1tgdIybKDV1JD6etX0AUehPFF0";
    private int counter = 0;
    private JSONArray postArray = null;
    private Context context;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private SwipyRefreshLayout swipeRefreshLayout;
    private String nextUrl = null;
    private int counterLimit;
    private int imageCount = 6;
    private String pageUrl;
    private TextView errorTextView;
    private Button errorButton;
    private RelativeLayout errorLayout;
    private RefreshableFragmentActivity activity; //so that we can update the view when changing sharedpreferences
    private DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //LinearLayout        llLayout    = (LinearLayout)    inflater.inflate(R.layout.fragment, container, false);
        View view = inflater.inflate(R.layout.newsfeed, container, false);
        db = new DatabaseHelper(getActivity().getApplicationContext());
        activity = (RefreshableFragmentActivity) getActivity();
        Bundle args = getArguments();
        pageUrl = args.getString("pageUrl", "internationalchaluunion");

        errorTextView = (TextView) view.findViewById(R.id.error_message_tv);
        errorButton = (Button) view.findViewById(R.id.error_button);
        errorLayout = (RelativeLayout) view.findViewById(R.id.error_layout);

        swipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
        scrollView = (ScrollView) view.findViewById(R.id.newsfeed_scrollview);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Rect scrollBounds = new Rect();
                scrollView.getHitRect(scrollBounds);
                if (linearLayout.getChildCount() != 0 && linearLayout.getChildAt(linearLayout.getChildCount() - 1).getLocalVisibleRect(scrollBounds) && !swipeRefreshLayout.isRefreshing()) {
                    // Any portion of the imageView, even a single pixel, is within the visible window

                    getNewPost();
                }
            }
        });


        context = getActivity();
        swipeRefreshLayout.setRefreshing(true);
        new RequestPost().execute(baseUrl + pageUrl + "/posts");
        swipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeue-Thin.otf");

        errorTextView.setTypeface(tf);
        errorButton.setTypeface(tf);


        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestPost().execute(baseUrl + pageUrl + "/posts");
                swipeRefreshLayout.setRefreshing(true);
                errorButton.setEnabled(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {

                getNewPost();
            }
        });

        return view;
    }

    private void getNewPost() {
        if (postArray != null) {
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


        } else {
            Log.v("test", "else part called");
            swipeRefreshLayout.setRefreshing(true);
            new RequestPost().execute(baseUrl + pageUrl + "/posts");
        }
    }

    public static Newsfeed newInstance(String pageUrl) {
        Newsfeed newsfeed = new Newsfeed();
        Bundle args_icu = new Bundle();
        args_icu.putString("pageUrl", pageUrl);
        newsfeed.setArguments(args_icu);
        return newsfeed;
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

            if (result != null) {
                errorLayout.setVisibility(View.GONE);
                new RequestID().execute();
            } else
                swipeRefreshLayout.setRefreshing(false);

            if (PostID == null) {//only happens when there is no conncetion while app is started
                errorLayout.setVisibility(View.VISIBLE);
                errorButton.setEnabled(true);
            }

        }
    }

    class RequestID extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... uri) {

            JSONObject json = null;
            if (PostID != null) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;

                HttpGet httpGet;
                httpGet = new HttpGet(baseUrl + PostID + "?fields=object_id,link");
                httpGet.setHeader("Authorization", "OAuth " + OAuth);
                try {
                    response = httpclient.execute(httpGet);
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));

                    //Log.v("object_id", responseString);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonResult) {
            super.onPostExecute(jsonResult);
            //Do anything with response..
            if (jsonResult != null) {
                try {
                    PhotoID = jsonResult.getString("object_id");
                    postUrl = jsonResult.getString("link");
                    new RequestImageURL().execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
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
            LayoutInflater vi = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View cardView = vi.inflate(R.layout.troll_card, null);
            if (result != null) {


                cardView.setTag(R.string.tag_photo_id, PhotoID);
                cardView.setTag(R.string.tag_post_url, postUrl);
                cardView.setTag(R.string.tag_clicked, false);
                final ImageView favourite = (ImageView) cardView.findViewById(R.id.favourite_button);

                final SharedPreferences favourites = PreferenceManager.getDefaultSharedPreferences(context);
                final SharedPreferences.Editor editor = favourites.edit();
                if (db.isInFavourite(PhotoID)) {
                    cardView.setTag(R.string.tag_clicked, true);
                    favourite.setImageResource(R.drawable.like);
                }
                DynamicImageView imageView = (DynamicImageView) cardView.findViewById(R.id.dynamic_image_view);
                //setting image resource
                imageView.setImageBitmap(result);
                //setting image position

                //imageView.setLayoutParams();
                //adding view to layout
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PhotoViewer.class);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bytes = stream.toByteArray();
                        intent.putExtra("BMP", bytes);
                        intent.putExtra("link", (String) cardView.getTag(R.string.tag_post_url));
                        startActivity(intent);
                    }
                });

                ImageView share = (ImageView) cardView.findViewById(R.id.whatsapp);
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
                        share.putExtra(Intent.EXTRA_TEXT, "Shared via Malayalam Trolls. http://bigaram.com/trollapp/");
                        share.setPackage("com.whatsapp");
                        startActivityForResult(Intent.createChooser(share, "Share!"), 0);
                    }
                });


                favourite.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (cardView.getTag(R.string.tag_clicked).equals(false)) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] bytes = stream.toByteArray();
                            String byteString = Base64.encodeToString(bytes, Base64.DEFAULT);
                            db.add((String) cardView.getTag(R.string.tag_photo_id), byteString, (String) cardView.getTag(R.string.tag_post_url),
                                    pageUrl);
                            favourite.setImageResource(R.drawable.like);
                            cardView.setTag(R.string.tag_clicked, true);
                        } else {
                            db.delete((String) cardView.getTag(R.string.tag_photo_id));
                            cardView.setTag(R.string.tag_clicked, false);
                            favourite.setImageResource(R.drawable.like_grey);
                        }
                        activity.refreshFavourites();
                    }

                });

                linearLayout.addView(cardView);

//
//                    scrollView.setSmoothScrollingEnabled(true);
//                    scrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            scrollView.smoothScrollTo(0, cardView.getTop());
//                        }
//                    });
//


                if (!favourites.getBoolean("doesntWantToRate", false) && counter == 10) {
                    AlertDialog dialog;
                    //following code will be in your activity.java file


                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Rate Us");
                    builder.setMessage("If you love our app, please take a moment to rate it.");
                    builder.setNegativeButton("Rate", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on OK
                            //  You can write the code  to save the selected item here

                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.procrastinators.pling")));
                            editor.putBoolean("doesntWantToRate", true);
                            editor.commit();
                        }
                    }).setNeutralButton("Not Now", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                            .setPositiveButton("No, Thanks", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Your code when user clicked on Cancel
                                    editor.putBoolean("doesntWantToRate", true);
                                    editor.commit();
                                }
                            });

                    dialog = builder.create();//AlertDialog dialog; create like this outside onClick
                    dialog.show();
                }
                counter++;
            } else
                Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            if (linearLayout.getChildCount() == 1)
                getNewPost();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("check", "delete");
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        f.delete();
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

}
