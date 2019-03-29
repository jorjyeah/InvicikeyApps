package xyz.jorjyeah.skripsi.invicikey;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView txtQrCodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    String prevIntentData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initViews();
    }

    private void initViews() {
        txtQrCodeValue = findViewById(R.id.txtQrCodeValue);
        surfaceView = findViewById(R.id.surfaceView);
    }

    private void initialiseDetectorAndSources(){

        Toast.makeText(getApplicationContext(),"Scanner Started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if(ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(),"Avoid memory leaks, scanner stopped",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size() != 0){
                    txtQrCodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            intentData = barcodes.valueAt(0).displayValue;
                            txtQrCodeValue.setText(intentData);
                            Globals globals = (Globals)getApplication();
                            if(globals.getToken()){
                                JSONObject reader = null;
                                try {
                                    reader = new JSONObject(intentData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String action = null;
                                try {
                                    action = reader.getString("action");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                switch(action){
                                    case "registration":
                                        globals.setToken(false);
                                        RegisModule.main(reader, getApplicationContext());
                                        if(!globals.getToken()){
                                            globals.setToken(true);
                                            finish();
                                        }
                                    case "authentication":
                                        // if auth,  go to auth
                                        break;
                                    default :
                                        Toast.makeText(ScanActivity.this, "Invalid format", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                // - in module regis or auth -
                                // if status 1, message = registerd -> toast message -> go to main activity
                                // if status -> toast message -> sent data again

                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initialiseDetectorAndSources();
    }
}
