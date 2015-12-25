package com.kakaxicm.geekming.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sean on 14-9-10.
 */
public class DBUtil {
    private static DBUtil mInstance;
    private Context mContext;
    private SQLHelper1 mSQLHelp;
    private SQLiteDatabase mSQLiteDatabase;
    public static final String EDITORINF_TABLE = "editorInf";
    public static final String SEARCHINF_TABLE = "searchInf";
    public static final String EDITORNAME = "name";
    public static final String NUMBER = "number";
    public static final String SEARCHTEXT = "searchtext";

    public static final String DB_NAME = "pdatabase.db";// 数据库名称

    private DBUtil(Context context) {
        mContext = context;
        mSQLHelp = new SQLHelper1(context);
        mSQLiteDatabase = mSQLHelp.getWritableDatabase();
    }

    /**
     * 初始化数据库操作DBUtil类
     */
    public synchronized static DBUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBUtil(context);
        }
        return mInstance;
    }

    /**
     * 关闭数据库
     */
    public synchronized void close() {
        if (mSQLHelp != null) {
            mSQLHelp.close();
            mSQLHelp = null;
        }
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
            mSQLiteDatabase = null;
        }
        mInstance = null;
    }

    public synchronized static void addEditor(Context context, String editorName) {
        if (editorName == null || editorName.isEmpty())
            return;
        DBUtil du = getInstance(context);
        ContentValues values = new ContentValues();
        Cursor cursor = du.selectData(EDITORINF_TABLE, new String[]{NUMBER}, EDITORNAME + "=?", new String[]{editorName}, null, null, null, "1");
        if (cursor != null && cursor.moveToNext()) {
            values.put(NUMBER, cursor.getInt(0) + 1);
            du.updateData(EDITORINF_TABLE, values, EDITORNAME + "=?", new String[]{editorName});
        } else {
            values.put(EDITORNAME, editorName);
            values.put(NUMBER, 1);
            du.insertData(EDITORINF_TABLE, values);
        }

        du.close();
    }

    public synchronized static void addSearchText(Context context, String searchText) {
        if (searchText == null || searchText.isEmpty())
            return;
        DBUtil du = getInstance(context);
        ContentValues values = new ContentValues();
        values.put(SEARCHTEXT, searchText);
        du.insertData(SEARCHINF_TABLE, values);
        du.close();


    }

    public synchronized static void cleanDB(Context context) {
        if (mInstance != null) {
            mInstance.close();
        }
        if (context != null) {
            context.deleteDatabase(DB_NAME);
        }
    }

    /**
     * 添加数据
     */
    public void insertData(String tableName, ContentValues values) {
        mSQLiteDatabase.insert(tableName, null, values);
    }

    /**
     * 更新数据
     *
     * @param values
     * @param whereClause
     * @param whereArgs
     */
    public void updateData(String tableName, ContentValues values, String whereClause,
                           String[] whereArgs) {
        mSQLiteDatabase.update(tableName, values, whereClause,
                whereArgs);
    }

    /**
     * 删除数据
     *
     * @param whereClause
     * @param whereArgs
     */
    public void deleteData(String tableName, String whereClause, String[] whereArgs) {
        mSQLiteDatabase
                .delete(tableName, whereClause, whereArgs);
    }

    /**
     * 查询数据
     *
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public synchronized Cursor selectData(String tableName, String[] columns, String selection,
                                          String[] selectionArgs, String groupBy, String having,
                                          String orderBy, String limit) {
        Cursor cursor = mSQLiteDatabase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        return cursor;
    }

    public class SQLHelper1 extends SQLiteOpenHelper {


        public static final int VERSION = 1;


        private Context context;

        public SQLHelper1(Context context) {
            super(context, DB_NAME, null, VERSION);
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table if not exists " + EDITORINF_TABLE +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    EDITORNAME + " TEXT , " +
                    NUMBER + " INTEGER)";
            db.execSQL(sql);
            sql = "create table if not exists " + SEARCHINF_TABLE +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SEARCHTEXT + " TEXT)";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }
    }
}
