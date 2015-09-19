package com.procrastinators.malayalammemes.malayalammemes;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Jazeem on 26/08/15.
 */
public class PhotoViewer extends Activity {
    private PhotoView photoView;
    private ImageView share;
    private ImageView viewOnFb;
    TrollApp application;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);
        Intent intent = getIntent();
        application = (TrollApp) getApplication();
        tracker = application.getDefaultTracker();
        byte[] bytes = intent.getByteArrayExtra("BMP");
        final String link = intent.getStringExtra("link");
        final String pageUrl = intent.getStringExtra("pageUrl");
        final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        photoView = (PhotoView)findViewById(R.id.photo_view);
        photoView.setImageBitmap(bmp);
        share = (ImageView)findViewById(R.id.whatsapp_share);
        viewOnFb = (ImageView) findViewById(R.id.view_on_fb);
        viewOnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(pageUrl)
                        .setAction("ViewOnFb")
                        .setLabel(link)
                        .build());
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(launchBrowser);
        }});

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("clicked", "photoview");
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = FileUtil.newTempFile();
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tracker.send(new HitBuilders.EventBuilder()
                            .setCategory(pageUrl)
                            .setAction("WhatsappShare")
                            .setLabel(link)
                            .build());
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                share.putExtra(Intent.EXTRA_TEXT,"Shared via Malayalam Trolls. http://bigaram.com/trollapp/");
                share.setPackage("com.whatsapp");
                startActivityForResult(Intent.createChooser(share, "Share!"), 0);

            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        tracker.setScreenName("PhotoViewer");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileUtil.deleteTempFiles();
    }
}
