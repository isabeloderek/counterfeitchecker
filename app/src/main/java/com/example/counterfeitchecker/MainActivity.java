package com.example.counterfeitchecker;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {
    String[] permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
    boolean storagePermission = false;
    boolean cameraPermission = false;
    String TAG = "Permission";
    ImageButton capture;
    Uri uriForCamera;

    private final ActivityResultLauncher<String> requestPermissionLauncherStorageImages =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, permissions[0] + " Granted");
                    storagePermission = true;
                } else {
                    Log.d(TAG, permissions[0] + " Not Granted");
                    storagePermission = false;
                }
                requestPermissionCameraAccess();
            });

    private final ActivityResultLauncher<String> requestPermissionLauncherCameraAccess =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, permissions[1] + " Granted");
                    storagePermission = true;
                } else {
                    Log.d(TAG, permissions[1] + " Not Granted");
                    storagePermission = false;
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        Intent resultIntent = new Intent(MainActivity.this, CaptureResult.class);
                        resultIntent.putExtra("capturedImage", uriForCamera);
                        startActivity(resultIntent);
                    } else {
                        Log.e(TAG, "Camera activity failed with code: " + o.getResultCode());
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capture = (ImageButton) findViewById(R.id.capture);
        //image = (ImageView) findViewById(R.id.image);

        if (!storagePermission) {
            requestPermissionStorageImages();
        }

        capture.setOnClickListener(v -> {
            if (cameraPermission) {
                openCamera();
            } else {
                requestPermissionCameraAccess();
            }
        });
    }

    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Counterfeit Checker");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Check bank notes for authenticity");
        uriForCamera = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForCamera);
        cameraLauncher.launch(cameraIntent);
    }

    public void requestPermissionStorageImages() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, permissions[0] + " Granted");
            storagePermission = true;
            requestPermissionCameraAccess();
        } else {
            requestPermissionLauncherStorageImages.launch(permissions[0]);
        }
    }

    public void requestPermissionCameraAccess() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, permissions[1] + " Granted");
            cameraPermission = true;
        } else {
            requestPermissionLauncherCameraAccess.launch(permissions[1]);
        }
    }
}