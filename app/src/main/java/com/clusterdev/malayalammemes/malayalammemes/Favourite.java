package com.clusterdev.malayalammemes.malayalammemes;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jazeem on 31/08/15.
 */
public class Favourite extends Fragment {




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourite, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    public void refreshView(){
        Log.v("test", "favourite on resume");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String byteArray = sharedPreferences.getString("BYTE_ARRAY", "");
        final LinearLayout linearLayout = (LinearLayout)getView().findViewById(R.id.linear_layout);
        linearLayout.removeAllViews();
        if (!byteArray.equals("")) {
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
            String byteEncoded = null;
            String photoID = null;
            Log.v("imageArray length",""+imageArray.length());
            for (int i=0; i<imageArray.length(); i++) {
                try {
                    byteEncoded = imageArray.getJSONObject(i).getString("byteArrayString");
                    photoID = imageArray.getJSONObject(i).getString("photoID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                byte[] array = Base64.decode(byteEncoded, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(array, 0, array.length);
                LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View card = vi.inflate(R.layout.troll_card, null);
                card.setTag(R.string.tag_photo_id, photoID);

                //ImageView Setup
                DynamicImageView imageView = (DynamicImageView) card.findViewById(R.id.dynamic_image_view);
                //setting image resource
                imageView.setImageBitmap(bmp);

                ImageView fav = (ImageView)card.findViewById(R.id.favourite_button);
                fav.setImageResource(R.drawable.like);

                final JSONArray finalImageArray = imageArray;
                final JSONObject finalJsonObject = jsonObject;
                fav.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        linearLayout.removeView(card);
                        try {

                            int i;

                            for (i=0; i< finalImageArray.length(); i++){

                                if (finalImageArray.getJSONObject(i).get("photoID").equals(card.getTag(R.string.tag_photo_id)))
                                    break;
                            }
                            if(i != finalImageArray.length())
                                finalImageArray.remove(i);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.putString("BYTE_ARRAY", finalJsonObject.toString());
                        editor.commit();

                    }
                });

                linearLayout.addView(card);
            }

        }
    }
}
