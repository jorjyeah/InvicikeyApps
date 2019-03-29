package xyz.jorjyeah.skripsi.invicikey;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AuthModule {


    private static String username;
    private static String appid;
    private static String challenge;
    private static String auth_portal;
    private static String keyhandle;
    private static String encryptedsalt;
    static boolean success = false;

//    public interface ResponseRegisterCallback {
//        void onSuccess(boolean value);
//        void onError(Throwable throwable);
//    }

    static public void main(JSONObject reader, final Context context){
        try {
            username = reader.getString("username");
            appid = reader.getString("appId");
            challenge = reader.getString("challenge");
            auth_portal = reader.getString("auth_portal");
            encryptedsalt = reader.getString("encryptedsalt");
            keyhandle = reader.getString("keyhandle");
            //sendToAuthPortal(username, challenge, auth_portal, keyhandle, encryptedsalt, context);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to register, Can't reach API", Toast.LENGTH_SHORT).show();
        }
    }

    static public void sendToAuthPortal(final String username, String challenge, String reg_portal, final String keyhandle, String keypub, final Context context){
        CredentialDBHelper credentialDBHelper;
        final SQLiteDatabase dbRead;
        final SQLiteDatabase dbWrite;
        credentialDBHelper = new CredentialDBHelper(context);
        dbRead = credentialDBHelper.getReadableDatabase();
        dbWrite = credentialDBHelper.getWritableDatabase();

        JSONObject signedJSON = new JSONObject();
        try {
            signedJSON.put("challenge", challenge);
            signedJSON.put("keyhandle",keyhandle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String signedencrypted = Encryption.encrypt(keyhandle,signedJSON.toString());

        final HashMap<String,String> hashMap = new HashMap<String, String>();
        hashMap.put("func","registrationFromApps");
        hashMap.put("keypub",keypub);
        hashMap.put("username",username);
        hashMap.put("signedencrypted",signedencrypted);
        APIService webServiceAPI = APIClient.getApiClient(reg_portal).create(APIService.class);
        Call<JsonElement> result = webServiceAPI.regisapi(hashMap);
        result.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonElement element = response.body();
                JsonObject object = element.getAsJsonObject();
                String responsemessage = object.get("message").getAsString();
                String responsestatus = object.get("status").getAsString();
                Toast.makeText(context, responsemessage, Toast.LENGTH_SHORT).show();
                if(responsestatus.equals("1")){
                    //block to sent another data from QRCODE Scanner
                    Globals globals = (Globals)context;
                    globals.setToken(false);
                    //safe to DB your code here, to give some delay
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(CredentialContract.CredentialEntry.COLUMN_NAME_keyhandle, keyhandle);
                    contentValues.put(CredentialContract.CredentialEntry.COLUMN_NAME_username, username);
                    contentValues.put(CredentialContract.CredentialEntry.COLUMN_NAME_appid, appid);
                    contentValues.put(CredentialContract.CredentialEntry.COLUMN_NAME_counter, 0);
                    long newRowId = dbWrite.insert(CredentialContract.CredentialEntry.TABLE_NAME,null,contentValues);
                    dbWrite.close();
//                    Intent mainActivity = new Intent(context, MainActivity.class);
//                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(mainActivity);
                }else{
                    //grant access to sent another data from QRCODE Scanner
                    //Toast.makeText(context, responsemessage, Toast.LENGTH_SHORT).show();
                    //delete the auto key pub and key handle
                    try {
                        Encryption.deleteEntry(keyhandle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Globals globals = (Globals)context;
                    globals.setToken(true);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(context, "Failed to register, API Reject "+t, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
