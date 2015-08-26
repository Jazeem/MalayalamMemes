package com.clusterdev.malayalammemes.malayalammemes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Jazeem on 26/08/15.
 */
public class PhotoViewer extends Activity {
    private PhotoView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("BMP");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        photoView = (PhotoView)findViewById(R.id.photo_view);
        photoView.setImageBitmap(bmp);

    }
}
