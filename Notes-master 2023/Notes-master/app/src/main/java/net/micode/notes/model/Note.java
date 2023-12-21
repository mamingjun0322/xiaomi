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

package net.micode.notes.model;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.CallNote;
import net.micode.notes.data.Notes.DataColumns;
import net.micode.notes.data.Notes.NoteColumns;
import net.micode.notes.data.Notes.TextNote;

import java.util.ArrayList;


public class Note {
    private ContentValues mNoteDiffValues;
    private NoteData mNoteData;
    private static final String TAG = "Note";

    /**
     * 为添加新的笔记到数据库创建一个新的笔记ID
     */
    public static synchronized long getNewNoteId(Context context, long folderId) {
        // 在数据库中创建一个新的笔记
        ContentValues values = new ContentValues();
        long createdTime = System.currentTimeMillis();
        values.put(NoteColumns.CREATED_DATE, createdTime);
        values.put(NoteColumns.MODIFIED_DATE, createdTime);
        values.put(NoteColumns.TYPE, Notes.TYPE_NOTE);
        values.put(NoteColumns.LOCAL_MODIFIED, 1);
        values.put(NoteColumns.PARENT_ID, folderId);
        Uri uri = context.getContentResolver().insert(Notes.CONTENT_NOTE_URI, values);

        long noteId = 0;
        try {
            noteId = Long.valueOf(uri.getPathSegments().get(1));
        } catch (NumberFormatException e) {
            Log.e(TAG, "获取笔记ID时出错：" + e.toString());
            noteId = 0;
        }
        if (noteId == -1) {
            throw new IllegalStateException("错误的笔记ID：" + noteId);
        }
        return noteId;
    }

    public Note() {
        mNoteDiffValues = new ContentValues();
        mNoteData = new NoteData();
    }

    // 设置笔记的值
    public void setNoteValue(String key, String value) {
        mNoteDiffValues.put(key, value);
        mNoteDiffValues.put(NoteColumns.LOCAL_MODIFIED, 1);
        mNoteDiffValues.put(NoteColumns.MODIFIED_DATE, System.currentTimeMillis());
    }

    // 设置文本数据
    public void setTextData(String key, String value) {
        mNoteData.setTextData(key, value);
    }

    // 设置文本数据ID
    public void setTextDataId(long id) {
        mNoteData.setTextDataId(id);
    }

    // 获取文本数据ID
    public long getTextDataId() {
        return mNoteData.mTextDataId;
    }

    // 设置通话数据ID
    public void setCallDataId(long id) {
        mNoteData.setCallDataId(id);
    }

    // 设置通话数据
    public void setCallData(String key, String value) {
        mNoteData.setCallData(key, value);
    }

    // 判断是否本地已修改
    public boolean isLocalModified() {
        return mNoteDiffValues.size() > 0 || mNoteData.isLocalModified();
    }

    // 同步笔记
    public boolean syncNote(Context context, long noteId) {
        if (noteId <= 0) {
            throw new IllegalArgumentException("错误的笔记ID：" + noteId);
        }

        if (!isLocalModified()) {
            return true;
        }

        /**
         * 理论上，一旦数据更改，就应该在 {@link NoteColumns#LOCAL_MODIFIED} 和 {@link NoteColumns#MODIFIED_DATE} 上更新笔记。
         * 为了数据的安全性，即使更新笔记失败，我们也会更新笔记数据信息。
         */
        if (context.getContentResolver().update(
                ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId), mNoteDiffValues, null,
                null) == 0) {
            Log.e(TAG, "更新笔记出错，不应该发生");
            // 不要返回，继续执行
        }
        mNoteDiffValues.clear();

        if (mNoteData.isLocalModified()
                && (mNoteData.pushIntoContentResolver(context, noteId) == null)) {
            return false;
        }

        return true;
    }

    private class NoteData {
        private long mTextDataId;

        private ContentValues mTextDataValues;

        private long mCallDataId;

        private ContentValues mCallDataValues;

        private static final String TAG = "NoteData";

        public NoteData() {
            mTextDataValues = new ContentValues();
            mCallDataValues = new ContentValues();
            mTextDataId = 0;
            mCallDataId = 0;
        }

        // 判断是否本地已修改
        boolean isLocalModified() {
            return mTextDataValues.size() > 0 || mCallDataValues.size() > 0;
        }

        // 设置文本数据ID
        void setTextDataId(long id) {
            if(id <= 0) {
                throw new IllegalArgumentException("文本数据ID应大于0");
            }
            mTextDataId = id;
        }

        // 设置通话数据ID
        void setCallDataId(long id) {
            if (id <= 0) {
                throw new IllegalArgumentException("通话数据ID应大于0");
            }
            mCallDataId = id;
        }

        // 设置通话数据
        void setCallData(String key, String value) {
            mCallDataValues.put(key, value);
            mNoteDiffValues.put(NoteColumns.LOCAL_MODIFIED, 1);
            mNoteDiffValues.put(NoteColumns.MODIFIED_DATE, System.currentTimeMillis());
        }

        // 设置文本数据
        void setTextData(String key, String value) {
            mTextDataValues.put(key, value);
            mNoteDiffValues.put(NoteColumns.LOCAL_MODIFIED, 1);
            mNoteDiffValues.put(NoteColumns.MODIFIED_DATE, System.currentTimeMillis());
        }

        // 将数据推送到ContentResolver中
        Uri pushIntoContentResolver(Context context, long noteId) {
            /**
             * 安全检查
             */
            if (noteId <= 0) {
                throw new IllegalArgumentException("错误的笔记ID：" + noteId);
            }

            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder builder = null;

            if(mTextDataValues.size() > 0) {
                mTextDataValues.put(DataColumns.NOTE_ID, noteId);
                if (mTextDataId == 0) {
                    mTextDataValues.put(DataColumns.MIME_TYPE, TextNote.CONTENT_ITEM_TYPE);
                    Uri uri = context.getContentResolver().insert(Notes.CONTENT_DATA_URI,
                            mTextDataValues);
                    try {
                        setTextDataId(Long.valueOf(uri.getPathSegments().get(1)));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "插入新文本数据失败，笔记ID：" + noteId);
                        mTextDataValues.clear();
                        return null;
                    }
                } else {
                    builder = ContentProviderOperation.newUpdate(ContentUris.withAppendedId(
                            Notes.CONTENT_DATA_URI, mTextDataId));
                    builder.withValues(mTextDataValues);
                    operationList.add(builder.build());
                }
                mTextDataValues.clear();
            }

            if(mCallDataValues.size() > 0) {
                mCallDataValues.put(DataColumns.NOTE_ID, noteId);
                if (mCallDataId == 0) {
                    mCallDataValues.put(DataColumns.MIME_TYPE, CallNote.CONTENT_ITEM_TYPE);
                    Uri uri = context.getContentResolver().insert(Notes.CONTENT_DATA_URI,
                            mCallDataValues);
                    try {
                        setCallDataId(Long.valueOf(uri.getPathSegments().get(1)));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "插入新通话数据失败，笔记ID：" + noteId);
                        mCallDataValues.clear();
                        return null;
                    }
                } else {
                    builder = ContentProviderOperation.newUpdate(ContentUris.withAppendedId(
                            Notes.CONTENT_DATA_URI, mCallDataId));
                    builder.withValues(mCallDataValues);
                    operationList.add(builder.build());
                }
                mCallDataValues.clear();
            }

            if (operationList.size() > 0) {
                try {
                    ContentProviderResult[] results = context.getContentResolver().applyBatch(
                            Notes.AUTHORITY, operationList);
                    return (results == null || results.length == 0 || results[0] == null) ? null
                            : ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId);
                } catch (RemoteException e) {
                    Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
                    return null;
                } catch (OperationApplicationException e) {
                    Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
                    return null;
                }
            }
            return null;
        }
    }
}
