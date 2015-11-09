package com.procrastinators.malayalammemes.malayalammemes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;

import java.io.File;

/**
 * Created by ajnas on 19/9/15.
 */
public class FileUtil {
    private static final String DIRECTORY_NAME = "Malayalam Trolls";

    private static String getDirectoryPath (){
        return Environment.getExternalStorageDirectory() +
                File.separator +DIRECTORY_NAME+File.separator;
    }

    public static File newTempFile(){
        File directory = new File(getDirectoryPath());
        if(!directory.exists())
            directory.mkdirs();

        long timeStamp = System.currentTimeMillis();
        final String filename = Long.toString(timeStamp)+".jpeg";
        File tempFile = new File(Environment.getExternalStorageDirectory() +
                File.separator +DIRECTORY_NAME+File.separator+filename);
        return tempFile;
    }

    public static void deleteTempFiles() {
        File directory = new File(getDirectoryPath());
        if (directory.isDirectory())
            for (File child : directory.listFiles())
                child.delete();
    }
    public static Bitmap combineImages(Bitmap c, Bitmap bm) {
        Bitmap cs = null;



        int width = c.getWidth();
        int height = c.getHeight();


        int footerHeight = width * bm.getHeight() / bm.getWidth();


        cs = Bitmap.createBitmap(width, height + footerHeight, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        Bitmap scaled = Bitmap.createScaledBitmap(bm, width, footerHeight, false);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(scaled, 0f, height, null);

        return cs;
    }

}
