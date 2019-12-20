package com.example.magisterka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    public static final String DATABASE_NAME = "Label.db";
    public static final String TABLE_NAME = "product_table2";
    public static final String TABLE_CREATE = "CREATE TABLE "+TABLE_NAME+" (" +
            "NAME"+" TEXT, " +
            "TODELETE" +" INTEGER DEFAULT 0, "+
            "IMAGE"+" TEXT);";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }


    public boolean insertData(String name, String image, int toDelete){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("IMAGE", image);
        contentValues.put("TODELETE", toDelete);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from  "+TABLE_NAME, null);
        return res;

    }

    public Integer deleteData (String name){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "NAME = ?",   new String[] {name});
    }
    public boolean  updateData(String name, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("IMAGE", image);
        db.update(TABLE_NAME, contentValues, "name=?", new String[]{name});
        return true;
    }
    public boolean  setToDelete(int toDelete, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TODELETE", toDelete);
        db.update(TABLE_NAME, contentValues, "name=?", new String[]{name});
        return true;
    }

}
