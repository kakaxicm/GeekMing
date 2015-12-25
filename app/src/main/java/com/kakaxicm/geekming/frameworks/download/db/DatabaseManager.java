package com.kakaxicm.geekming.frameworks.download.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.kakaxicm.geekming.frameworks.download.DownloadTask;
import com.kakaxicm.geekming.frameworks.download.DownloadTaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 将 XmDownloadInfo 信息写入数据库
 * 
 * @author Gaoyuan
 * 
 */
public class DatabaseManager {

	private static DatabaseManager mDatabaseManager;
	private DownloadSQLiteHelper mSQLiteHelper;

	public static DatabaseManager getInstance(Context context) {
		if (mDatabaseManager == null) {
			synchronized (DatabaseManager.class) {
				if (mDatabaseManager == null) {
					mDatabaseManager = new DatabaseManager(context);
				}
			}
		}
		return mDatabaseManager;
	}

	private DatabaseManager(Context ctx) {
		mSQLiteHelper = new DownloadSQLiteHelper(ctx);
	}

	/** 把任务加到数据库中 */
	public long addTaskToDb(DownloadTaskInfo info) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DownloadDBColumns.URL, info.getmDownloadUrl());
		contentValues.put(DownloadDBColumns.NAME, info.getmName());
		contentValues.put(DownloadDBColumns.SAVE_PATH, info.mLocalPath);
		contentValues.put(DownloadDBColumns.DOWNLOADED_SIZE, 0);
		contentValues.put(DownloadDBColumns.TOTAL_SIZE, -1);
		contentValues.put(DownloadDBColumns.DOWNLOAD_STATUS, DownloadTask.STATE_WAIT);
		return mSQLiteHelper.getWritableDatabase().insert(DownloadSQLiteHelper.DOWNLOAD_TABLE_NAME, null, contentValues);
	}

	public void removeTaskFromDb(DownloadTaskInfo info) {
		mSQLiteHelper.getWritableDatabase().delete(DownloadSQLiteHelper.DOWNLOAD_TABLE_NAME,
				DownloadDBColumns._ID + "=" + info.mId, null);
	}

	public void updateTaskTotalLengthToDb(long infoId, long totalSize) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DownloadDBColumns.TOTAL_SIZE, totalSize);
		mSQLiteHelper.getWritableDatabase().update(DownloadSQLiteHelper.DOWNLOAD_TABLE_NAME, contentValues,
				DownloadDBColumns._ID + "=" + infoId, null);
	}

	public void updateTaskProgressToDb(long infoId, long currertSize, int status) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DownloadDBColumns.DOWNLOADED_SIZE, currertSize);
		contentValues.put(DownloadDBColumns.DOWNLOAD_STATUS, status);
		mSQLiteHelper.getWritableDatabase().update(DownloadSQLiteHelper.DOWNLOAD_TABLE_NAME, contentValues,
				DownloadDBColumns._ID + "=" + infoId, null);
	}

	public void updateTaskStateToDb(long infoId, int status) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DownloadDBColumns.DOWNLOAD_STATUS, status);
		mSQLiteHelper.getWritableDatabase().update(DownloadSQLiteHelper.DOWNLOAD_TABLE_NAME, contentValues,
				DownloadDBColumns._ID + "=" + infoId, null);
	}

	/**
	 * 从数据库中查找任务，用于冷恢复任务
	 * @return
     */
	public List<DownloadTaskInfo> queryAllTasksFromDb() {
		Cursor cursor = mSQLiteHelper.getReadableDatabase().query(DownloadSQLiteHelper.DOWNLOAD_TABLE_NAME, new String[] {}, null, null,
				null, null, null);
		List<DownloadTaskInfo> infos = new ArrayList<>();
		DownloadTaskInfo info = null;
		while(cursor.moveToNext()) {
			int id = cursor.getInt(0);//ID
			String localPath = cursor.getString(3);//LOCAL_PATH
			long curLen = cursor.getLong(4);//CURLEN
			long totalLen = cursor.getLong(5);//TOTALLEN
			int state = cursor.getInt(6);//STATE
			info = new DownloadTaskInfo(cursor.getString(1), cursor.getString(2));
			info.mId = id;
			info.mLocalPath = localPath;
			info.mCurrentSize = curLen;
			info.mTotalSize = totalLen;
			info.mState = state;
			infos.add(info);
		}
		return infos;
	}

	/**
	 * 根据Url查询对应的任务
	 * @param url
	 * @return
     */
	public DownloadTaskInfo queryTaskByUrlFromDb(String url) {
		Cursor cursor = mSQLiteHelper.getReadableDatabase().query(DownloadSQLiteHelper.DOWNLOAD_TABLE_NAME, new String[] {}, DownloadDBColumns.URL+"=?", new String[]{url},
				null, null, null);
		DownloadTaskInfo info = null;
		while(cursor.moveToNext()) {
			int s0 = cursor.getInt(0);//ID
			String name = cursor.getString(1);//NAME
			String localPath = cursor.getString(3);//LOCAL_PATH
			long curLen = cursor.getLong(4);//CURLEN
			long totalLen = cursor.getLong(5);//TOTALLEN
			int state = cursor.getInt(6);//STATE
			info = new DownloadTaskInfo(name, url);
			info.mId = s0;
			info.mLocalPath = localPath;
			info.mCurrentSize = curLen;
			info.mTotalSize = totalLen;
			info.mState = state;
			return info;
		}
		return null;
	}

}
