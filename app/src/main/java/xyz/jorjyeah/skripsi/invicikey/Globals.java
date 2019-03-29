package xyz.jorjyeah.skripsi.invicikey;

import android.app.Application;

public class Globals extends Application {
    private boolean token = true;

    public boolean getToken(){
        return this.token;
    }

    public void setToken(boolean val){
        this.token = val;
    }
}
