/*
 * 版权所有 (c) 2010-2011，The MiCode开源社区（www.micode.net）
 *
 * 根据Apache许可证2.0版（“许可证”）获得许可；
 * 除非符合许可证，否则您不得使用此文件。
 * 您可以在以下网址获得许可证副本
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，否则软件
 * 根据许可证分发的基础上是“按原样”的，
 * 没有任何明示或暗示的保证或条件。
 * 有关特定语言的许可证的管理权限，请参见许可证。
 */

package net.micode.notes.gtask.remote;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class GTaskSyncService extends Service {
    public final static String ACTION_STRING_NAME = "sync_action_type";

    public final static int ACTION_START_SYNC = 0;

    public final static int ACTION_CANCEL_SYNC = 1;

    public final static int ACTION_INVALID = 2;

    public final static String GTASK_SERVICE_BROADCAST_NAME = "net.micode.notes.gtask.remote.gtask_sync_service";

    public final static String GTASK_SERVICE_BROADCAST_IS_SYNCING = "isSyncing";

    public final static String GTASK_SERVICE_BROADCAST_PROGRESS_MSG = "progressMsg";

    private static GTaskASyncTask mSyncTask = null;

    private static String mSyncProgress = "";

    // 启动同步
    private void startSync() {
        if (mSyncTask == null) {
            mSyncTask = new GTaskASyncTask(this, new GTaskASyncTask.OnCompleteListener() {
                public void onComplete() {
                    mSyncTask = null;
                    sendBroadcast("");
                    stopSelf();
                }
            });
            sendBroadcast("");
            mSyncTask.execute();
        }
    }

    // 取消同步
    private void cancelSync() {
        if (mSyncTask != null) {
            mSyncTask.cancelSync();
        }
    }

    @Override
    public void onCreate() {
        mSyncTask = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey(ACTION_STRING_NAME)) {
            switch (bundle.getInt(ACTION_STRING_NAME, ACTION_INVALID)) {
                case ACTION_START_SYNC:
                    startSync();
                    break;
                case ACTION_CANCEL_SYNC:
                    cancelSync();
                    break;
                default:
                    break;
            }
            return START_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // 在内存不足时取消同步
    @Override
    public void onLowMemory() {
        if (mSyncTask != null) {
            mSyncTask.cancelSync();
        }
    }

    // 绑定服务
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 发送广播通知同步进度
    public void sendBroadcast(String msg) {
        mSyncProgress = msg;
        Intent intent = new Intent(GTASK_SERVICE_BROADCAST_NAME);
        intent.putExtra(GTASK_SERVICE_BROADCAST_IS_SYNCING, mSyncTask != null);
        intent.putExtra(GTASK_SERVICE_BROADCAST_PROGRESS_MSG, msg);
        sendBroadcast(intent);
    }

    // 启动同步的静态方法，接收Activity参数
    public static void startSync(Activity activity) {
        GTaskManager.getInstance().setActivityContext(activity);
        Intent intent = new Intent(activity, GTaskSyncService.class);
        intent.putExtra(GTaskSyncService.ACTION_STRING_NAME, GTaskSyncService.ACTION_START_SYNC);
        activity.startService(intent);
    }

    // 取消同步的静态方法，接收Context参数
    public static void cancelSync(Context context) {
        Intent intent = new Intent(context, GTaskSyncService.class);
        intent.putExtra(GTaskSyncService.ACTION_STRING_NAME, GTaskSyncService.ACTION_CANCEL_SYNC);
        context.startService(intent);
    }

    // 判断是否正在同步的静态方法
    public static boolean isSyncing() {
        return mSyncTask != null;
    }

    // 获取同步进度的静态方法
    public static String getProgressString() {
        return mSyncProgress;
    }
}
