/*
 * 版权所有（c）2010-2011年，The MiCode开源社区（www.micode.net）
 *
 * 根据Apache License，版本2.0许可证（“许可证”）;
 * 除非符合许可证，否则您不得使用此文件。
 * 您可以在以下地址获取许可证副本
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，本软件是根据“按原样”提供的，
 * 没有任何明示或暗示的保证或条件，包括但不限于，
 * 适销性或特定用途适用性的保证和条件。详见许可证。
 * 您可能获得的唯一保证是由于您选择使用或不使用本软件而由
 * Open Source Community 给予的那些保证。
 */

package net.micode.notes.gtask.data;

import android.database.Cursor;

import org.json.JSONObject;

public abstract class Node {
    public static final int SYNC_ACTION_NONE = 0;

    public static final int SYNC_ACTION_ADD_REMOTE = 1;

    public static final int SYNC_ACTION_ADD_LOCAL = 2;

    public static final int SYNC_ACTION_DEL_REMOTE = 3;

    public static final int SYNC_ACTION_DEL_LOCAL = 4;

    public static final int SYNC_ACTION_UPDATE_REMOTE = 5;

    public static final int SYNC_ACTION_UPDATE_LOCAL = 6;

    public static final int SYNC_ACTION_UPDATE_CONFLICT = 7;

    public static final int SYNC_ACTION_ERROR = 8;

    private String mGid;  // Google 任务的唯一标识符

    private String mName;  // 任务名称

    private long mLastModified;  // 最后修改时间

    private boolean mDeleted;  // 任务是否被删除

    public Node() {
        mGid = null;
        mName = "";
        mLastModified = 0;
        mDeleted = false;
    }

    /**
     * 获取创建动作的JSON对象
     *
     * @param actionId 动作ID
     * @return JSON对象
     */
    public abstract JSONObject getCreateAction(int actionId);

    /**
     * 获取更新动作的JSON对象
     *
     * @param actionId 动作ID
     * @return JSON对象
     */
    public abstract JSONObject getUpdateAction(int actionId);

    /**
     * 根据远程JSON内容设置节点内容
     *
     * @param js 远程JSON对象
     */
    public abstract void setContentByRemoteJSON(JSONObject js);

    /**
     * 根据本地JSON内容设置节点内容
     *
     * @param js 本地JSON对象
     */
    public abstract void setContentByLocalJSON(JSONObject js);

    /**
     * 从节点内容获取本地JSON对象
     *
     * @return 本地JSON对象
     */
    public abstract JSONObject getLocalJSONFromContent();

    /**
     * 获取同步动作
     *
     * @param c 数据库游标
     * @return 同步动作
     */
    public abstract int getSyncAction(Cursor c);

    /**
     * 设置节点的Google任务唯一标识符
     *
     * @param gid Google 任务唯一标识符
     */
    public void setGid(String gid) {
        this.mGid = gid;
    }

    /**
     * 设置节点名称
     *
     * @param name 节点名称
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * 设置节点最后修改时间
     *
     * @param lastModified 最后修改时间
     */
    public void setLastModified(long lastModified) {
        this.mLastModified = lastModified;
    }

    /**
     * 设置节点是否被删除
     *
     * @param deleted 是否被删除
     */
    public void setDeleted(boolean deleted) {
        this.mDeleted = deleted;
    }

    /**
     * 获取节点的Google任务唯一标识符
     *
     * @return Google 任务唯一标识符
     */
    public String getGid() {
        return this.mGid;
    }

    /**
     * 获取节点名称
     *
     * @return 节点名称
     */
    public String getName() {
        return this.mName;
    }

    /**
     * 获取节点最后修改时间
     *
     * @return 最后修改时间
     */
    public long getLastModified() {
        return this.mLastModified;
    }

    /**
     * 获取节点是否被删除
     *
     * @return 是否被删除
     */
    public boolean getDeleted() {
        return this.mDeleted;
    }

}
