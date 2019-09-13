package com.example.antonellab.sms_app;

import android.provider.BaseColumns;

/**
 * Created by AntonellaB on 01-Nov-16.
 */

public class MessagesDBSchema implements BaseColumns {
    public static final String TABLE_NAME = "smsTable";
    public static final String COLUMN_NAME_MESSAGE = "message";
    public static final String COLUMN_NAME_PHONE = "phone";
    public static final String COLUMN_NAME_TIME = "timestamp";
    public static final String COLUMN_NAME_TYPE = "type";
}