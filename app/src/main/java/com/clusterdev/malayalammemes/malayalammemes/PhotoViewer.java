package com.clusterdev.malayalammemes.malayalammemes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("BMP");
        final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        photoView = (PhotoView)findViewById(R.id.photo_view);
        photoView.setImageBitmap(bmp);
        share = (ImageView)findViewById(R.id.whatsapp_share);
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
                share.putExtra(Intent.EXTRA_TEXT,"Awesome troll send through awesome app made by awesome developer");
                share.setPackage("com.whatsapp");
                startActivityForResult(Intent.createChooser(share, "Share!"), 0);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("check", "delete");
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        f.delete();
    }
}
