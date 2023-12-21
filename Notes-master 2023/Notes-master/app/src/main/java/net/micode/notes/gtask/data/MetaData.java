/*
 * 版权所有（c）2010-2011年，The MiCode开源社区（www.micode.net）
 *
 * 根据Apache License，版本2.0许可证（“许可证”）;
 * 除非符合许可证，否则您不得使用此文件。
 * 您可以在以下地址获取许可证副本
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，软件
 * 根据许可证分发的原始基础上分发，
 * 不附带任何形式的明示或暗示的担保或条件。
 * 请参阅许可证以了解特定的语言管理权限和
 * 许可证下的限制。
 */

package net.micode.notes.gtask.data;

import android.database.Cursor;
import android.util.Log;

import net.micode.notes.tool.GTaskStringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * MetaData类表示任务的元数据，用于在Google任务中存储附加信息。
 * 该类扩展了Task类，以便添加与Google任务相关的元数据。
 */
public class MetaData extends Task {
    private final static String TAG = MetaData.class.getSimpleName();

    private String mRelatedGid = null;

    /**
     * 设置元数据，将关联的GID和元信息存储为任务的注释。
     * @param gid Google任务的唯一标识符
     * @param metaInfo 包含元信息的JSON对象
     */
    public void setMeta(String gid, JSONObject metaInfo) {
        try {
            metaInfo.put(GTaskStringUtils.META_HEAD_GTASK_ID, gid);
        } catch (JSONException e) {
            Log.e(TAG, "failed to put related gid");
        }
        setNotes(metaInfo.toString());
        setName(GTaskStringUtils.META_NOTE_NAME);
    }

    /**
     * 获取与该元数据关联的Google任务的唯一标识符（GID）。
     * @return 与元数据关联的Google任务的GID
     */
    public String getRelatedGid() {
        return mRelatedGid;
    }

    /**
     * 指示元数据是否值得保存。如果元数据的注释不为空，则值得保存。
     * @return 如果值得保存，则为true；否则为false
     */
    @Override
    public boolean isWorthSaving() {
        return getNotes() != null;
    }

    /**
     * 根据从远程JSON数据中获取的内容设置元数据的内容。
     * @param js 包含元数据信息的JSON对象
     */
    @Override
    public void setContentByRemoteJSON(JSONObject js) {
        super.setContentByRemoteJSON(js);
        if (getNotes() != null) {
            try {
                JSONObject metaInfo = new JSONObject(getNotes().trim());
                mRelatedGid = metaInfo.getString(GTaskStringUtils.META_HEAD_GTASK_ID);
            } catch (JSONException e) {
                Log.w(TAG, "failed to get related gid");
                mRelatedGid = null;
            }
        }
    }

    /**
     * 此函数不应被调用。元数据的本地JSON内容由远程数据设置，而不是由本地数据设置。
     */
    @Override
    public void setContentByLocalJSON(JSONObject js) {
        // 此函数不应被调用
        throw new IllegalAccessError("MetaData:setContentByLocalJSON should not be called");
    }

    /**
     * 此函数不应被调用。元数据的本地JSON内容由远程数据设置，而不是由本地数据设置。
     */
    @Override
    public JSONObject getLocalJSONFromContent() {
        throw new IllegalAccessError("MetaData:getLocalJSONFromContent should not be called");
    }

    /**
     * 此函数不应被调用。元数据的同步操作由远程数据控制，而不是由本地数据控制。
     */
    @Override
    public int getSyncAction(Cursor c) {
        throw new IllegalAccessError("MetaData:getSyncAction should not be called");
    }
}
