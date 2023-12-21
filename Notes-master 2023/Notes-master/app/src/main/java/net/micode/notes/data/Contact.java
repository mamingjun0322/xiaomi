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

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.util.HashMap;

public class Contact {
    // 联系人缓存，用于存储已查询的联系人信息，以避免重复查询
    private static HashMap<String, String> sContactCache;
    private static final String TAG = "Contact";

    // 用于查询联系人的选择条件，基于电话号码和数据类型
    private static final String CALLER_ID_SELECTION = "PHONE_NUMBERS_EQUAL(" + Phone.NUMBER
            + ",?) AND " + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'"
            + " AND " + Data.RAW_CONTACT_ID + " IN "
            + "(SELECT raw_contact_id "
            + " FROM phone_lookup"
            + " WHERE min_match = '+')";

    // 获取联系人名称的方法，通过给定的电话号码和应用上下文
    public static String getContact(Context context, String phoneNumber) {
        // 如果联系人缓存为空，初始化它
        if(sContactCache == null) {
            sContactCache = new HashMap<String, String>();
        }

        // 如果联系人缓存中已存在给定电话号码的信息，直接返回
        if(sContactCache.containsKey(phoneNumber)) {
            return sContactCache.get(phoneNumber);
        }

        // 替换选择条件中的占位符，使用最小匹配规则
        String selection = CALLER_ID_SELECTION.replace("+",
                PhoneNumberUtils.toCallerIDMinMatch(phoneNumber));

        // 使用内容解析器查询联系人数据
        Cursor cursor = context.getContentResolver().query(
                Data.CONTENT_URI,
                new String [] { Phone.DISPLAY_NAME },
                selection,
                new String[] { phoneNumber },
                null);

        // 如果查询结果非空并且可以移动到第一行
        if (cursor != null && cursor.moveToFirst()) {
            try {
                // 获取联系人姓名
                String name = cursor.getString(0);
                // 将结果存入缓存
                sContactCache.put(phoneNumber, name);
                // 返回联系人姓名
                return name;
            } catch (IndexOutOfBoundsException e) {
                // 处理可能的异常情况
                Log.e(TAG, " Cursor get string error " + e.toString());
                return null;
            } finally {
                // 关闭游标
                cursor.close();
            }
        } else {
            // 如果没有匹配的联系人，记录日志并返回null
            Log.d(TAG, "No contact matched with number:" + phoneNumber);
            return null;
        }
    }
}
