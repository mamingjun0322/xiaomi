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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import net.micode.notes.R;
import net.micode.notes.ui.NotesListActivity;
import net.micode.notes.ui.NotesPreferenceActivity;

public class GTaskASyncTask extends AsyncTask<Void, String, Integer> {

    private static int GTASK_SYNC_NOTIFICATION_ID = 5234235;

    // 定义任务完成时的回调接口
    public interface OnCompleteListener {
        void onComplete();
    }

    private Context mContext;
    private NotificationManager mNotifiManager;
    private GTaskManager mTaskManager;
    private OnCompleteListener mOnCompleteListener;

    // 构造函数，接收上下文和完成监听器
    public GTaskASyncTask(Context context, OnCompleteListener listener) {
        mContext = context;
        mOnCompleteListener = listener;
        mNotifiManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mTaskManager = GTaskManager.getInstance();
    }

    // 取消同步操作
    public void cancelSync() {
        mTaskManager.cancelSync();
    }

    // 发布同步进度信息
    public void publishProgess(String message) {
        publishProgress(new String[] { message });
    }

    // 显示通知
    private void showNotification(int tickerId, String content) {
        Notification notification = new Notification(R.drawable.notification,
                mContext.getString(tickerId), System.currentTimeMillis());
        notification.defaults = Notification.DEFAULT_LIGHTS;
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        PendingIntent pendingIntent;
        if (tickerId != R.string.ticker_success) {
            pendingIntent = PendingIntent.getActivity(mContext, 0,
                    new Intent(mContext, NotesPreferenceActivity.class), 0);
        } else {
            pendingIntent = PendingIntent.getActivity(mContext, 0,
                    new Intent(mContext, NotesListActivity.class), 0);
        }
        notification.setLatestEventInfo(mContext, mContext.getString(R.string.app_name),
                content, pendingIntent);

        mNotifiManager.notify(GTASK_SYNC_NOTIFICATION_ID, notification);
    }

    // 后台执行任务
    @Override
    protected Integer doInBackground(Void... unused) {
        publishProgess(mContext.getString(R.string.sync_progress_login,
                NotesPreferenceActivity.getSyncAccountName(mContext)));
        return mTaskManager.sync(mContext, this);
    }

    // 更新同步进度
    @Override
    protected void onProgressUpdate(String... progress) {
        showNotification(R.string.ticker_syncing, progress[0]);
        if (mContext instanceof GTaskSyncService) {
            ((GTaskSyncService) mContext).sendBroadcast(progress[0]);
        }
    }

    // 任务执行完成后调用
    @Override
    protected void onPostExecute(Integer result) {
        if (result == GTaskManager.STATE_SUCCESS) {
            showNotification(R.string.ticker_success,
                    mContext.getString(R.string.success_sync_account,
                            mTaskManager.getSyncAccount()));
            NotesPreferenceActivity.setLastSyncTime(mContext, System.currentTimeMillis());
        } else if (result == GTaskManager.STATE_NETWORK_ERROR) {
            showNotification(R.string.ticker_fail,
                    mContext.getString(R.string.error_sync_network));
        } else if (result == GTaskManager.STATE_INTERNAL_ERROR) {
            showNotification(R.string.ticker_fail,
                    mContext.getString(R.string.error_sync_internal));
        } else if (result == GTaskManager.STATE_SYNC_CANCELLED) {
            showNotification(R.string.ticker_cancel,
                    mContext.getString(R.string.error_sync_cancelled));
        }
        if (mOnCompleteListener != null) {
            new Thread(new Runnable() {
                public void run() {
                    mOnCompleteListener.onComplete();
                }
            }).start();
        }
    }
}
