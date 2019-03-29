package xyz.jorjyeah.skripsi.invicikey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnScan,btnKeyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        btnScan = findViewById(R.id.btnScan);
        btnKeyList = findViewById(R.id.btnKeyList);
        btnScan.setOnClickListener(this);
        btnKeyList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnScan:
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
                break;
            case R.id.btnKeyList:
                startActivity(new Intent(MainActivity.this, KeyListActivity.class));
                break;
        }
    }
}
