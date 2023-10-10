package com.example.myapplication.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql="CREATE TABLE alarm_flag(flag INTEGER)";
        db.execSQL(createSql);
        ContentValues values = new ContentValues();
        values.put("flag",0);
        db.insert("alarm_flag",null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean getFlag(){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean flag = false;

        String[] projection = {"flag"};
        Cursor cursor = db.query("alarm_flag", projection, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int flagColumnIndex = cursor.getColumnIndex("flag");
            flag = (cursor.getInt(flagColumnIndex) == 1); // Assuming 1 represents true
            cursor.close();
        }
        db.close();
        return flag;
    }
    public void updateFlag(int newValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("flag", newValue);

        db.update("alarm_flag", values, null, null);

        db.close();
    }
}
