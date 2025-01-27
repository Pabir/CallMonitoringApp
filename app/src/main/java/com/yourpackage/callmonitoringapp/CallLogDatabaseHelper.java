package com.yourpackage.callmonitoringapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class CallLogDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "call_log_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "call_logs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_CALL_TYPE = "call_type";
    private static final String COLUMN_DURATION = "duration";

    public CallLogDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

   @Override
public void onCreate(SQLiteDatabase db) {
    String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PHONE_NUMBER + " TEXT, "  // Make phone number unique
            + COLUMN_CALL_TYPE + " TEXT, "
            + COLUMN_DURATION + " INTEGER" + ")";
    db.execSQL(CREATE_TABLE);
}


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addCall(String phoneNumber, String callType, long duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(COLUMN_CALL_TYPE, callType);
        values.put(COLUMN_DURATION, duration);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<CallLog> getAllCalls() {
        ArrayList<CallLog> callLogs = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER));
                String callType = cursor.getString(cursor.getColumnIndex(COLUMN_CALL_TYPE));
                long duration = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION));
                callLogs.add(new CallLog(phoneNumber, callType, duration));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return callLogs;
    }
}
