package com.kakaxicm.geekming.frameworks.download;

import android.content.Context;

import com.kakaxicm.geekming.frameworks.download.db.DatabaseManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by kakaxicm on 2015/12/21.
 */
public class DownloadManager {
    private Context mContext;
    /** 正在下载队列  */
    private LinkedList<DownloadTask> mDownloadingTaskList;
    /** 等待队列 */
    private LinkedList<DownloadTask> mWaitingTaskList;
    /** 暂停队列 */
    private LinkedList<DownloadTask> mPauseTaskList;
    /** 下载任务map */
    private HashMap<Long, DownloadTask> mTaskMap;
    /** 下载线程池 */
    private ThreadPoolExecutor mThreadPool;
    /** 最大同时下载数 */
    private int mMaxDownloadingTaskSize = 3;

    private int mMaxQueueSize = 20; // 最大线程数
    private int mCorePoolSize = 5;
    private int mMaxPoolSize = 5;
    private long mKeepAliveTime = 1L;

    private static volatile DownloadManager mDownloadManager;

    public static DownloadManager getInstance(Context ctx) {
        if (mDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (mDownloadManager == null) {
                    mDownloadManager = new DownloadManager(ctx);
                }
            }
        }
        return mDownloadManager;
    }

    private DownloadManager(Context context) {
        mContext = context;
        mDownloadingTaskList = new LinkedList<>();
        mWaitingTaskList = new LinkedList<>();
        mPauseTaskList = new LinkedList<>();
        mTaskMap = new HashMap<>();
        mThreadPool = new ThreadPoolExecutor(mCorePoolSize, mMaxPoolSize, mKeepAliveTime, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(mMaxQueueSize));
        File dir = new File(DownloadConfig.DOWNLOAD_PATH);
        if(!dir.exists()){
            dir.mkdirs();
        }
    }

    /**
     * 添加下载任务
     * @param info
     * @return
     */
    public synchronized long addTask(DownloadTaskInfo info) {
        //1.构建task
        //2.判断任务map有没有这个任务，没有添加到map中
        //3.状态设为wait
        //4.添加到数据库中，获取ID
        //5id付给info
        //6调度下一个任务
        DownloadTask task = new DownloadTask(mContext, info);
        if(!mTaskMap.containsKey(task.mInfo.mId)) {
            mWaitingTaskList.add(task);
            info.mState = DownloadTask.STATE_WAIT;
            long id = DatabaseManager.getInstance(mContext).addTaskToDb(info);
            task.mInfo.mId = id;
            mTaskMap.put(id, task);
            executeNextTask();
            return id;
        }
        return -1L;
    }

    /**
     * 暂停任务
     * @param task
     */
    public synchronized void stopTask(DownloadTask task) {
        //1.判断下载任务的状态
        //   1.1. STATE_WAIT, 将任务的状态改为 STATE_STOP, 调用任务的 stop 方法, 并将其从等待队列中移除
        //   1.2. STATE_DOWNLOADING, 将状态改为STATE_STOP, 调用 stop 方法, 从线程池中移除, 从执行队列中移除
        //2.将任务加到暂停队列中
        //3.执行下一个任务
        //4.更新任务状态到数据库

        if(task != null) {
            switch (task.mInfo.mState) {
                case DownloadTask.STATE_WAIT:
                    task.mInfo.mState = DownloadTask.STATE_STOP;
                    task.stop();
                    mWaitingTaskList.remove(task);
                    break;
                case DownloadTask.STATE_DOWNLOADING:
                    task.mInfo.mState = DownloadTask.STATE_STOP;
                    task.stop();
                    mThreadPool.remove(task.mDownloadThread);
                    mDownloadingTaskList.remove(task);
                    break;
                default:
                    break;
            }
            mPauseTaskList.addLast(task);
            //更新数据库状态
            DatabaseManager.getInstance(mContext).updateTaskStateToDb(task.mInfo.mId, DownloadTask.STATE_STOP);
            executeNextTask();
        }
    }

    /**
     * 重新开始任务(热启动)
     * @param id
     */
    public synchronized void restartTaskFromMemory(long id) {
        //1 STATE = stop || fail 暂停队列出列
        //2.更新任务状态到内存和数据库
        //3,等待队列入队
        DownloadTask task = getDownloadTaskByIdFromMemory(id);
        if(task != null) {
            if(task.mInfo.mState == DownloadTask.STATE_STOP || task.mInfo.mState == DownloadTask.STATE_FAIL) {
                if(mPauseTaskList.contains(task)) {
                    mPauseTaskList.remove(task);
                    if(!mWaitingTaskList.contains(task)) {
                        task.onWait();
                        task.mInfo.mState = DownloadTask.STATE_WAIT;
                        DatabaseManager.getInstance(mContext).updateTaskStateToDb(id, DownloadTask.STATE_WAIT);
                        mWaitingTaskList.addLast(task);
                        executeNextTask();
                    }
                }
            }
        }
    }

    /**
     *从数据库中冷启动下载任务，加入该任务已经在调度队列中，则走正常的restart方法
     */
    public synchronized void restartDownloadTask(DownloadTaskInfo taskInfo) {
        //1.判断该任务是否处于调度中
        //  1.1 处于则直接restart
        //  1.2.不处于, 则
        //    1>>>>构建新的task, 状态更新为wait 更新到数据库
        //    2>>>>添加到map中,加入等待队列
        //    3>>>>调度下一个任务

        long id = taskInfo.mId;
        if(mTaskMap.containsKey(id)) {
            restartTaskFromMemory(id);
        } else {
            DownloadTask task = new DownloadTask(mContext, taskInfo);
            task.mInfo.mState = DownloadTask.STATE_WAIT;
            DatabaseManager.getInstance(mContext).updateTaskStateToDb(id, DownloadTask.STATE_WAIT);
            mTaskMap.put(id, task);
            mWaitingTaskList.add(task);
            executeNextTask();
        }
    }

    /**
     * 完成任务, 此方法无需客户端调用
     * @param task XmDownloadTask 对象
     */
    public synchronized void finishTask(DownloadTask task) {
        // 1. 从任务map中移除
        // 2. 从执行队列中移除
        // 3. 从数据库中移除
        // 4. 执行下一个任务
        if (task != null) {
            mTaskMap.remove(task.mInfo.mId);
            mDownloadingTaskList.remove(task);
            executeNextTask();
        }
    }

    /**
     * 暂停任务
     * @param taskId 下载任务id
     */
    public synchronized void stopTask(Long taskId) {
        DownloadTask task = getDownloadTaskByIdFromMemory(taskId);
        if (task != null) {
            stopTask(task);
        }
    }

    /**
     * 根据任务id 获取任务对象，在任务正在下载，再次进来时需要恢复任务,实时监听它的状态,类似于机器的热启动
     * @param id
     * @return XmDownloadTask对象
     */
    public DownloadTask getDownloadTaskByIdFromMemory(Long id) {
        return mTaskMap.get(id);
    }

    /**
     * 查询本地url对应的任务
     * @param context
     * @param url
     * @return
     */
    public DownloadTaskInfo getDownloadTaskByUrlFromDb(Context context, String url) {
       return DatabaseManager.getInstance(context).queryTaskByUrlFromDb(url);
    }

    /**
     * 获取当前正在调度的所有任务
     * @return
     */
    public synchronized List<DownloadTask> getAllTasksFromMemory() {
        List<DownloadTask> tasks = new ArrayList<>();
        for(Long id : mTaskMap.keySet()) {
            tasks.add(getDownloadTaskByIdFromMemory(id));
        }
        return tasks;
    }

    /**
     * 调度任务
     */
    private synchronized void executeNextTask() {
        //1.获取等待队列中的第一个任务
        //2. 如果执行队列的大小小于最大执行数量, 并且执行队列中不包含当前任务, 才开始执行
        if(mWaitingTaskList.size() > 0) {
            DownloadTask task = mWaitingTaskList.getFirst();
            if(task != null) {
                if(mDownloadingTaskList.size() < mMaxDownloadingTaskSize && !mDownloadingTaskList.contains(task)) {
                    mDownloadingTaskList.add(task);
                    task.mInfo.mState = DownloadTask.STATE_DOWNLOADING;
                    task.start();
                    mThreadPool.execute(task.mDownloadThread);
                    mWaitingTaskList.removeFirst();
                }
            }
        }
    }

    /**
     * 处理失败任务
     * 1.将失败任务从正在下载的任务中移除
     * 2.加入暂停队列
     * @param id
     */
    public synchronized void handlerFailedTask(long id) {
        DownloadTask task = getDownloadTaskByIdFromMemory(id);
        if(task != null) {
            if(task.mInfo.mState == DownloadTask.STATE_FAIL) {
                if(mDownloadingTaskList.contains(task)) {
                    mDownloadingTaskList.remove(task);
                }

                if(!mPauseTaskList.contains(task)) {
                    mPauseTaskList.add(task);
                }
            }
        }
    }

    /**
     * 移除(取消任务)
     * @param task XmDownloadTask 对象
     */
    public synchronized void removeTask(DownloadTask task) {
        // 1. 判断下载任务状态
        //     1.1. STATE_WAIT, 从等待队列中移除即可
        //     1.2. STATE_DOWNLOADING, 先将任务停下来(改状态, 调stop), 再从线程池中移除, 再从执行队列中移除
        //     1.3. STATE_STOP, 直接从暂停队列中移除.
        // 2. 从任务map里移除
        // 3. 从数据库里移除
        // 4. 执行下一个任务
        if (task != null) {
            switch (task.mInfo.mState) {
                case DownloadTask.STATE_WAIT:
                    mWaitingTaskList.remove(task);
                    break;
                case DownloadTask.STATE_DOWNLOADING:
                    task.mInfo.mState = DownloadTask.STATE_STOP;
                    task.stop();
                    mThreadPool.remove(task.mDownloadThread);
                    mDownloadingTaskList.remove(task);
                    break;
                case DownloadTask.STATE_STOP:
                    mPauseTaskList.remove(task);
                    break;
                default:
                    break;
            }
            mTaskMap.remove(task);
            DatabaseManager.getInstance(mContext).removeTaskFromDb(task.mInfo);
            executeNextTask();
        }
    }
}
