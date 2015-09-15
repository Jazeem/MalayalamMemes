package com.procrastinators.malayalammemes.malayalammemes;

/**
 * Created by ajnas on 16/9/15.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class that wraps the most common database operations. This example assumes you want a single table and data entity
 * with two properties: a title and a priority as an integer. Modify in all relevant locations if you need other/more
 * properties for your data and/or additional tables.
 */
public class DatabaseHelper {
    private SQLiteOpenHelper _openHelper;

    /**
     * Construct a new database helper object
     *
     * @param context The current context for the application or activity
     */
    public DatabaseHelper(Context context) {
        _openHelper = new SimpleSQLiteOpenHelper(context);
    }

    /**
     * This is an internal class that handles the creation of all database tables
     */
    class SimpleSQLiteOpenHelper extends SQLiteOpenHelper {
        SimpleSQLiteOpenHelper(Context context) {
            super(context, "main.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table favourite(photo_id TEXT UNIQUE, byte_array TEXT, link TEXT, page_url TEXT ,id INTEGER PRIMARY KEY AUTOINCREMENT)");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    /**
     * Return a cursor object with all rows in the table.
     *
     * @return A cursor suitable for use in a SimpleCursorAdapter
     */
    public Cursor getAll() {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        return db.rawQuery("select * from favourite order by id", null);
    }

    public boolean isInFavourite(String photoID){
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if(db==null)
            return false;
        else
        {
            Cursor cursor = db.rawQuery("select photo_id from favourite where photo_id="+photoID,null);
            if(cursor!=null && cursor.getCount()>0)
                return true;
            else
                return false;

        }
    }
    /**
     * Return values for a single row with the specified id
     *
     * @param id The unique id for the row o fetch
     * @return All column values are stored as properties in the ContentValues object
     */
    public ContentValues get(long id) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        ContentValues row = new ContentValues();
        Cursor cur = db.rawQuery("select title, priority from todos where _id = ?", new String[]{String.valueOf(id)});
        if (cur.moveToNext()) {
            row.put("title", cur.getString(0));
            row.put("priority", cur.getInt(1));
        }
        cur.close();
        db.close();
        return row;
    }

    /**
     * Add a new row to the database table
     *
     * @param title    The title value for the new row
     * @param priority The priority value for the new row
     * @return The unique id of the newly added row
     */
    public long add(String photoID,String byteArrayString,String link,String pageUrl) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        ContentValues row = new ContentValues();
        row.put("photo_id",photoID);
        row.put("byte_array",byteArrayString);
        row.put("link",link);
        row.put("page_url",pageUrl);
        long id = db.insert("favourite", null, row);
        db.close();
        return id;
    }

    /**
     * Delete the specified row from the database table. For simplicity reasons, nothing happens if
     * this operation fails.
     *
     * @param id The unique id for the row to delete
     */
    public void delete(String photoID) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete("favourite", "photo_id = ?", new String[]{photoID});
        db.close();
    }

    /**
     * Updates a row in the database table with new column values, without changing the unique id of the row.
     * For simplicity reasons, nothing happens if this operation fails.
     *
     * @param id       The unique id of the row to update
     * @param title    The new title value
     * @param priority The new priority value
     */
    public void update(long id, String title, int priority) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        ContentValues row = new ContentValues();
        row.put("title", title);
        row.put("priority", priority);
        db.update("todos", row, "_id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}