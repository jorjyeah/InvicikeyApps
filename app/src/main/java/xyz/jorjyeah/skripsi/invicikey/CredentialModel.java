package xyz.jorjyeah.skripsi.invicikey;

public class CredentialModel {
    private String username;
    private String appid;
    private String keyhandle;
    private int id;
    public CredentialModel(String username, String appid, String keyhandle){
        this.username=username;
        this.appid=appid;
        this.keyhandle=keyhandle;
    }

    public String getUsername(){ return username; }

    public void setUsername(String username){ this.username = username;}

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyhandle() {
        return keyhandle;
    }

    public void setKeyhandle(String keyhandle) {
        this.keyhandle = keyhandle;
    }
}
