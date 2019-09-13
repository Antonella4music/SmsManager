package com.example.antonellab.sms_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AntonellaB on 01-Nov-16.
 */

public class MessagesDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SMSapp.db";

    public MessagesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_ENTRIES =
            " CREATE TABLE " + MessagesDBSchema.TABLE_NAME +
            " (" + MessagesDBSchema._ID + " INTEGER PRIMARY KEY ," +
            MessagesDBSchema.COLUMN_NAME_MESSAGE + " TEXT, " +
            MessagesDBSchema.COLUMN_NAME_PHONE + " TEXT, " +
            MessagesDBSchema.COLUMN_NAME_TIME + " TEXT, " +
            MessagesDBSchema.COLUMN_NAME_TYPE + " INTEGER)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static final String subquery = "SELECT * FROM smsTable s1 " +
            " WHERE " +  MessagesDBSchema.COLUMN_NAME_TIME  + " = (SELECT MAX( " +  MessagesDBSchema.COLUMN_NAME_TIME + " )" +
            " FROM " +MessagesDBSchema.TABLE_NAME + " s2" +
            " WHERE s2." + MessagesDBSchema.COLUMN_NAME_PHONE + " = s1." +  MessagesDBSchema.COLUMN_NAME_PHONE + ") order by " + MessagesDBSchema.COLUMN_NAME_TIME + " desc ";

    private static final String query = "SELECT * FROM smsTable s1" +
            " WHERE " + MessagesDBSchema.COLUMN_NAME_PHONE + " = ? ";

    public boolean insertSms(String message, String phone, Integer type, long timeStamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesDBSchema.COLUMN_NAME_MESSAGE, message);
        values.put(MessagesDBSchema.COLUMN_NAME_PHONE, phone);
        values.put(MessagesDBSchema.COLUMN_NAME_TIME, timeStamp);
        values.put(MessagesDBSchema.COLUMN_NAME_TYPE, type);

        db.insert(MessagesDBSchema.TABLE_NAME, null, values);

        return true;
    }

    public Cursor getSMS() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(subquery, null);
    }

    public Cursor getSMSAfterPhone(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, new String[]{phoneNumber});
    }
}