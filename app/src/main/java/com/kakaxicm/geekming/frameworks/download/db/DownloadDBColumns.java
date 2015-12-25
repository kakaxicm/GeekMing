package com.kakaxicm.geekming.frameworks.download.db;

import android.provider.BaseColumns;

public class DownloadDBColumns implements BaseColumns {

	/** 下载文件名 */
	public static final String NAME = "name";
	/** 下载地址 */
	public static final String URL = "url";
	/** 本地保存路径 */
	public static final String SAVE_PATH = "save_path";
	/** 总大小 */
	public static final String TOTAL_SIZE = "total_size";
	/** 已下载大小 */
	public static final String DOWNLOADED_SIZE = "downloaded_size";
	/** 下载状态 */
	public static final String DOWNLOAD_STATUS = "download_status";
}
