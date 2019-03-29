package xyz.jorjyeah.skripsi.invicikey;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class CredentialDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "invicikey.db";
    private static final int DATABASE_VERSION = 1;
    ArrayList<CredentialModel> credentialData;

    public CredentialDBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CredentialContract.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV){
        db.execSQL(CredentialContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldV, int newV){
        onUpgrade(db, oldV, newV);
    }

    public void deleteCredential(String keyhandle,int id,Context context){
        SQLiteDatabase db =  getWritableDatabase();
        String selection = CredentialContract.CredentialEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(CredentialContract.CredentialEntry.TABLE_NAME,selection,selectionArgs);
//        db.execSQL("UPDATE "+ CredentialContract.CredentialEntry.TABLE_NAME+" SET "+selection+" -1 "+" WHERE "+ );
        try {
            Encryption.deleteEntry(keyhandle);
            Toast.makeText(context, "Key Deleted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        refreshKeyList();
        db.close();
    }
//
//    private void refreshKeyList() {
//        if(credentialData!=null){
//            credentialData.clear();
//        }
//        new credential
//        credentialData = getCredential();
//    }

    public ArrayList<CredentialModel> getCredential(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                CredentialContract.CredentialEntry._ID,
                CredentialContract.CredentialEntry.COLUMN_NAME_username,
                CredentialContract.CredentialEntry.COLUMN_NAME_appid,
                CredentialContract.CredentialEntry.COLUMN_NAME_keyhandle
        };

        Cursor cursor = db.query(
                CredentialContract.CredentialEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                CredentialContract.CredentialEntry._ID+" ASC"
        );
//        StringBuffer stringBuffer = new StringBuffer();
        ArrayList<CredentialModel> data = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                CredentialModel temp = new CredentialModel(
                        cursor.getString(cursor.getColumnIndex(CredentialContract.CredentialEntry.COLUMN_NAME_username)),
                        cursor.getString(cursor.getColumnIndex(CredentialContract.CredentialEntry.COLUMN_NAME_appid)),
                        cursor.getString(cursor.getColumnIndex(CredentialContract.CredentialEntry.COLUMN_NAME_keyhandle))
                );
//                stringBuffer.append();
                data.add(temp);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return data;
    }
}
