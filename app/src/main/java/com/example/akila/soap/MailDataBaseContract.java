package com.example.akila.soap;

import android.provider.BaseColumns;

public final class MailDataBaseContract {

    private MailDataBaseContract() {
    }

    public static class MailEntry implements BaseColumns {
        public static final String TABLE_NAME = "mailDB";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_SENDER = "sender";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_FLAG = "flag";
    }

}
