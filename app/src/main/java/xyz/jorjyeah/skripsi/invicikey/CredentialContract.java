package xyz.jorjyeah.skripsi.invicikey;

import android.provider.BaseColumns;

public class CredentialContract {
    private CredentialContract(){ }
    public static class CredentialEntry implements BaseColumns {
        public static final String TABLE_NAME="credential";
        public static final String COLUMN_NAME_keyhandle="keyhandle";
        public static final String COLUMN_NAME_username="username";
        public static final String COLUMN_NAME_appid="appid";
        public static final String COLUMN_NAME_counter="counter";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CredentialEntry.TABLE_NAME + " (" +
                    CredentialEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CredentialEntry.COLUMN_NAME_keyhandle + " TEXT," +
                    CredentialEntry.COLUMN_NAME_username + " TEXT," +
                    CredentialEntry.COLUMN_NAME_appid + " TEXT," +
                    CredentialEntry.COLUMN_NAME_counter + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CredentialEntry.TABLE_NAME;
}
