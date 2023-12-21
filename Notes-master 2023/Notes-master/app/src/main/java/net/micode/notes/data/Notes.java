/*
 * 版权所有 (c) 2010-2011，小米开源社区 (www.micode.net)
 *
 * 根据Apache许可证2.0版（“许可证”）获得许可;
 * 除非符合许可证的规定，否则不得使用此文件。
 * 您可以在以下网址获取许可证副本
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，否则本软件根据“原样”分发，
 * 不附带任何明示或暗示的保证或条件。
 * 有关特定语言的许可，请参阅许可证。
 */

package net.micode.notes.data;

import android.net.Uri;

public class Notes {
    // 权限标识符，用于标识提供数据的应用程序
    public static final String AUTHORITY = "micode_notes";
    // 日志标签
    public static final String TAG = "Notes";
    // 笔记类型常量
    public static final int TYPE_NOTE     = 0;
    public static final int TYPE_FOLDER   = 1;
    public static final int TYPE_SYSTEM   = 2;

    /**
     * 系统文件夹的标识符
     * {@link Notes#ID_ROOT_FOLDER } 是默认文件夹
     * {@link Notes#ID_TEMPARAY_FOLDER } 用于不属于任何文件夹的笔记
     * {@link Notes#ID_CALL_RECORD_FOLDER} 用于存储通话记录
     * {@link Notes#ID_TRASH_FOLER} 用于存储已删除的笔记
     */
    public static final int ID_ROOT_FOLDER = 0;
    public static final int ID_TEMPARAY_FOLDER = -1;
    public static final int ID_CALL_RECORD_FOLDER = -2;
    public static final int ID_TRASH_FOLER = -3;

    // 意图附加数据的键名
    public static final String INTENT_EXTRA_ALERT_DATE = "net.micode.notes.alert_date";
    public static final String INTENT_EXTRA_BACKGROUND_ID = "net.micode.notes.background_color_id";
    public static final String INTENT_EXTRA_WIDGET_ID = "net.micode.notes.widget_id";
    public static final String INTENT_EXTRA_WIDGET_TYPE = "net.micode.notes.widget_type";
    public static final String INTENT_EXTRA_FOLDER_ID = "net.micode.notes.folder_id";
    public static final String INTENT_EXTRA_CALL_DATE = "net.micode.notes.call_date";

    // 笔记小部件类型常量
    public static final int TYPE_WIDGET_INVALIDE      = -1;
    public static final int TYPE_WIDGET_2X            = 0;
    public static final int TYPE_WIDGET_4X            = 1;

    // 数据类型常量
    public static class DataConstants {
        public static final String NOTE = TextNote.CONTENT_ITEM_TYPE;
        public static final String CALL_NOTE = CallNote.CONTENT_ITEM_TYPE;
    }

    /**
     * 查询所有笔记和文件夹的Uri
     */
    public static final Uri CONTENT_NOTE_URI = Uri.parse("content://" + AUTHORITY + "/note");

    /**
     * 查询数据的Uri
     */
    public static final Uri CONTENT_DATA_URI = Uri.parse("content://" + AUTHORITY + "/data");

    // 笔记列的接口
    public interface NoteColumns {
        /**
         * 行的唯一ID
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String ID = "_id";

        /**
         * 笔记或文件夹的父级ID
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String PARENT_ID = "parent_id";

        /**
         * 笔记或文件夹的创建日期
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String CREATED_DATE = "created_date";

        /**
         * 最新修改日期
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String MODIFIED_DATE = "modified_date";

        /**
         * 提醒日期
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String ALERTED_DATE = "alert_date";

        /**
         * 文件夹的名称或笔记的文本内容
         * <P> 类型：TEXT </P>
         */
        public static final String SNIPPET = "snippet";

        /**
         * 笔记的小部件ID
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String WIDGET_ID = "widget_id";

        /**
         * 笔记的小部件类型
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String WIDGET_TYPE = "widget_type";

        /**
         * 笔记的背景颜色ID
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String BG_COLOR_ID = "bg_color_id";

        /**
         * 对于文本笔记，它没有附件，对于多媒体笔记，它至少有一个附件
         * <P> 类型：INTEGER </P>
         */
        public static final String HAS_ATTACHMENT = "has_attachment";

        /**
         * 笔记的文件夹计数
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String NOTES_COUNT = "notes_count";

        /**
         * 文件类型：文件夹或笔记
         * <P> 类型：INTEGER </P>
         */
        public static final String TYPE = "type";

        /**
         * 最后同步的ID
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String SYNC_ID = "sync_id";

        /**
         * 用于指示是否本地修改的标志
         * <P> 类型：INTEGER </P>
         */
        public static final String LOCAL_MODIFIED = "local_modified";

        /**
         * 移入临时文件夹之前的原始父ID
         * <P> 类型：INTEGER </P>
         */
        public static final String ORIGIN_PARENT_ID = "origin_parent_id";

        /**
         * GTask ID
         * <P> 类型：TEXT </P>
         */
        public static final String GTASK_ID = "gtask_id";

        /**
         * 版本代码
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String VERSION = "version";
    }

    // 数据列的接口
    public interface DataColumns {
        /**
         * 行的唯一ID
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String ID = "_id";

        /**
         * 此行表示的项的MIME类型。
         * <P> 类型：Text </P>
         */
        public static final String MIME_TYPE = "mime_type";

        /**
         * 指向该数据所属的笔记的引用ID
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String NOTE_ID = "note_id";

        /**
         * 笔记或文件夹的创建日期
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String CREATED_DATE = "created_date";

        /**
         * 最新修改日期
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String MODIFIED_DATE = "modified_date";

        /**
         * 数据的内容
         * <P> 类型：TEXT </P>
         */
        public static final String CONTENT = "content";

        /**
         * 通用数据列，含义是 {@link #MIMETYPE} 特定的，用于整数数据类型
         * <P> 类型：INTEGER </P>
         */
        public static final String DATA1 = "data1";

        /**
         * 通用数据列，含义是 {@link #MIMETYPE} 特定的，用于整数数据类型
         * <P> 类型：INTEGER </P>
         */
        public static final String DATA2 = "data2";

        /**
         * 通用数据列，含义是 {@link #MIMETYPE} 特定的，用于TEXT数据类型
         * <P> 类型：TEXT </P>
         */
        public static final String DATA3 = "data3";

        /**
         * 通用数据列，含义是 {@link #MIMETYPE} 特定的，用于TEXT数据类型
         * <P> 类型：TEXT </P>
         */
        public static final String DATA4 = "data4";

        /**
         * 通用数据列，含义是 {@link #MIMETYPE} 特定的，用于TEXT数据类型
         * <P> 类型：TEXT </P>
         */
        public static final String DATA5 = "data5";
    }

    // 文本笔记的具体实现
    public static final class TextNote implements DataColumns {
        /**
         * 模式，表示文本是检查列表模式还是正常模式
         * <P> 类型：Integer 1:检查列表模式 0: 正常模式 </P>
         */
        public static final String MODE = DATA1;

        public static final int MODE_CHECK_LIST = 1;

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/text_note";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/text_note";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/text_note");
    }

    // 通话记录的具体实现
    public static final class CallNote implements DataColumns {
        /**
         * 记录的通话日期
         * <P> 类型：INTEGER（long） </P>
         */
        public static final String CALL_DATE = DATA1;

        /**
         * 记录的电话号码
         * <P> 类型：TEXT </P>
         */
        public static final String PHONE_NUMBER = DATA3;

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/call_note";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/call_note";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/call_note");
    }
}
