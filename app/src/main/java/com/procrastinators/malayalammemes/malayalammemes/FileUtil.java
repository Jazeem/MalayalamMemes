package com.procrastinators.malayalammemes.malayalammemes;

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
}
