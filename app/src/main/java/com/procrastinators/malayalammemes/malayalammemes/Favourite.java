package com.procrastinators.malayalammemes.malayalammemes;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jazeem on 31/08/15.
 */
public class Favourite extends Fragment {


    private RefreshableFragmentActivity activity;
    private TextView favouriteTextView;
    private RelativeLayout favouriteLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourite, container, false);
        activity = (RefreshableFragmentActivity)getActivity();
        favouriteTextView = (TextView)view.findViewById(R.id.favourite_message_tv);
        favouriteLayout = (RelativeLayout)view.findViewById(R.id.no_favourite_layout);

        Typeface tf=Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeue-Thin.otf");
        favouriteTextView.setTypeface(tf);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    public void refreshView(){
        Log.v("test", "favourite on resume");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String byteArray = sharedPreferences.getString("BYTE_ARRAY", "");

        final LinearLayout linearLayout = (LinearLayout)getView().findViewById(R.id.linear_layout);
        linearLayout.removeAllViews();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(byteArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray imageArray = new JSONArray();
        try {
            imageArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (imageArray.length() != 0) {
            favouriteLayout.setVisibility(View.GONE);

            String byteEncoded = null;
            String photoID = null;
            Log.v("imageArray length",""+imageArray.length());
            for (int i=0; i<imageArray.length(); i++) {
                String link = null;
                try {
                    byteEncoded = imageArray.getJSONObject(i).getString("byteArrayString");
                    photoID = imageArray.getJSONObject(i).getString("photoID");
                    link = imageArray.getJSONObject(i).getString("link");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                byte[] array = Base64.decode(byteEncoded, Base64.DEFAULT);
                final Bitmap bmp = BitmapFactory.decodeByteArray(array, 0, array.length);
                LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View card = vi.inflate(R.layout.troll_card, null);
                card.setTag(R.string.tag_photo_id, photoID);

                //ImageView Setup
                DynamicImageView imageView = (DynamicImageView) card.findViewById(R.id.dynamic_image_view);
                //setting image resource
                imageView.setImageBitmap(bmp);
                final String finalLink = link;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PhotoViewer.class);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bytes = stream.toByteArray();
                        intent.putExtra("BMP",bytes);
                        intent.putExtra("link", finalLink);
                        startActivity(intent);
                    }
                });

                ImageView fav = (ImageView)card.findViewById(R.id.favourite_button);
                fav.setImageResource(R.drawable.like);

                ImageView share = (ImageView)card.findViewById(R.id.whatsapp);
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("clicked", "photoview");
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/*");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
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
                        share.putExtra(Intent.EXTRA_TEXT,"Shared via Malayalam Trolls. http://bigaram.com/trollapp/");
                        share.setPackage("com.whatsapp");
                        startActivityForResult(Intent.createChooser(share, "Share!"), 0);

                    }
                });

                final JSONObject finalJsonObject = jsonObject;
                fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        linearLayout.removeView(card);
                        JSONObject jsonObject1 = finalJsonObject;
                        JSONArray finalImageArray = null;
                        try {
                            finalImageArray = jsonObject1.getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String pageUrl = null;
                        try {

                            int i;

                            for (i=0; i< finalImageArray.length(); i++){

                                if (finalImageArray.getJSONObject(i).get("photoID").equals(card.getTag(R.string.tag_photo_id)))
                                    break;
                            }
                            if(i != finalImageArray.length()) {
                                pageUrl = finalImageArray.getJSONObject(i).get("pageUrl").toString();
                                JSONArray newJsonArray = JSONArrayHelper.remove(i, finalImageArray);

                                if(newJsonArray.length() == 0)
                                    favouriteLayout.setVisibility(View.VISIBLE);

                                jsonObject1.put("data",newJsonArray);

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LinearLayout newsfeedLinearLayout;
                        if(pageUrl.equals("internationalchaluunion"))
                            newsfeedLinearLayout = activity.getICULinearLayout();
                        else
                            newsfeedLinearLayout = activity.getTrollMalayalamLinearLayout();
                        int i;
                        for (i=0;i<newsfeedLinearLayout.getChildCount();i++){
                            if(newsfeedLinearLayout.getChildAt(i).getTag(R.string.tag_photo_id).equals(card.getTag(R.string.tag_photo_id)))
                                break;
                        }
                        if (i != newsfeedLinearLayout.getChildCount()){
                            View cardFound = newsfeedLinearLayout.getChildAt(i);
                            ImageView imageViewFound = (ImageView) cardFound.findViewById(R.id.favourite_button);
                            imageViewFound.setImageResource(R.drawable.like_grey);

                        }

                        editor.putString("BYTE_ARRAY",jsonObject1.toString());
                        editor.commit();

                    }

                });

                linearLayout.addView(card, 0);
            }

        }
        else{
            favouriteLayout.setVisibility(View.VISIBLE);
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
