package xyz.jorjyeah.skripsi.invicikey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class KeyListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CredentialAdapter credentialAdapter;
    private ArrayList<KeyListModel> keyListModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_list);

        addData();

        recyclerView = findViewById(R.id.recycler_view);
        credentialAdapter = new CredentialAdapter(keyListModelArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(KeyListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(credentialAdapter);
    }

    private void addData() {

        CredentialDBHelper credentialDBHelper = new CredentialDBHelper(KeyListActivity.this);
        ArrayList<CredentialModel> database;
        database = credentialDBHelper.getCredential();

        keyListModelArrayList = new ArrayList<>();
        for (int i = 0; i < database.size(); i++){
            keyListModelArrayList.add(new KeyListModel(database.get(i).getUsername(),database.get(i).getAppid(),database.get(i).getId(),database.get(i).getKeyhandle()));
        }
    }
}
