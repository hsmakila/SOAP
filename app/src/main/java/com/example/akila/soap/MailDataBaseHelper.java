package com.example.akila.soap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.akila.soap.MailDataBaseContract.MailEntry;

import java.util.Date;

public class MailDataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mails.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MailEntry.TABLE_NAME + " (" +
                    MailEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    MailEntry.COLUMN_NAME_SUBJECT + " TEXT," +
                    MailEntry.COLUMN_NAME_SENDER + " TEXT," +
                    MailEntry.COLUMN_NAME_CONTENT + " TEXT," +
                    MailEntry.COLUMN_NAME_DATE + " DATE," +
                    MailEntry.COLUMN_NAME_FLAG + " INTEGER DEFAULT 0)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MailEntry.TABLE_NAME;


    MailDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static String addMail(long ID, String SUBJECT, String SENDER, String CONTENT, Date DATE) {

        return "INSERT INTO " +
                MailEntry.TABLE_NAME +
                " (" +
                MailEntry.COLUMN_NAME_ID + "," +
                MailEntry.COLUMN_NAME_SUBJECT + "," +
                MailEntry.COLUMN_NAME_SENDER + "," +
                MailEntry.COLUMN_NAME_CONTENT + "," +
                MailEntry.COLUMN_NAME_DATE +
                ") " +
                "SELECT " +
                "'" + String.valueOf(ID) + "'," +
                "'" + SUBJECT + "'," +
                "'" + SENDER + "'," +
                "'" + CONTENT + "'," +
                "'" + DATE + "' " +
                "WHERE NOT EXISTS(SELECT * FROM " + MailEntry.TABLE_NAME + " WHERE " + MailEntry.TABLE_NAME + "." + MailEntry.COLUMN_NAME_ID + "=" + ID + ")";

    }

    public static String getMaxID() {
        return "SELECT MAX(" + MailEntry.COLUMN_NAME_ID + ") FROM " + MailEntry.TABLE_NAME;
    }

    public static String setFlag(long UID) {
        return "UPDATE " + MailEntry.TABLE_NAME + " SET " +
                MailEntry.COLUMN_NAME_FLAG + " = 1 WHERE " + MailEntry.TABLE_NAME + "." + MailEntry.COLUMN_NAME_ID + "=" + UID;
    }
    public static String getFlag(long UID) {
        return "SELECT " + MailEntry.COLUMN_NAME_FLAG + " FROM " +
                MailEntry.TABLE_NAME + " WHERE " + MailEntry.TABLE_NAME + "." + MailEntry.COLUMN_NAME_ID + "=" + UID;
    }
    public static String removeFlag(long UID) {
        return "UPDATE " + MailEntry.TABLE_NAME + " SET " +
                MailEntry.COLUMN_NAME_FLAG + " = 0 WHERE " + MailEntry.TABLE_NAME + "." + MailEntry.COLUMN_NAME_ID + "=" + UID;
    }

    public static String setAllDone(){
        return "UPDATE " + MailEntry.TABLE_NAME + " SET " +
                MailEntry.COLUMN_NAME_FLAG + " = 1";
    }

}
