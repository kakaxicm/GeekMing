package com.kakaxicm.geekming.frameworks.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloadSQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "download.db";
	private static final int DATABASE_VERSION = 2;
	public static final String DOWNLOAD_TABLE_NAME = "download_info";

	public DownloadSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DOWNLOAD_TABLE_NAME + " ( " 
				+ DownloadDBColumns._ID + " INTEGER PRIMARY KEY,"
				+ DownloadDBColumns.NAME + " TEXT,"
				+ DownloadDBColumns.URL + " TEXT,"
				+ DownloadDBColumns.SAVE_PATH + " TEXT,"
				+ DownloadDBColumns.DOWNLOADED_SIZE + " LONG,"
				+ DownloadDBColumns.TOTAL_SIZE + " LONG,"
				+ DownloadDBColumns.DOWNLOAD_STATUS + " INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ DOWNLOAD_TABLE_NAME);
		onCreate(db);
	}
}
