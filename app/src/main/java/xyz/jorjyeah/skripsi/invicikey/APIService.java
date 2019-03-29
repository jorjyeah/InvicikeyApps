package xyz.jorjyeah.skripsi.invicikey;

import com.google.gson.JsonElement;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

    @FormUrlEncoded
    @POST("reg_portal.php")
    Call<JsonElement> regisapi(@FieldMap HashMap<String, String> params);
}
