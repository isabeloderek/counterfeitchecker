package com.example.counterfeitchecker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CaptureResult extends AppCompatActivity {
    ImageView capturedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        capturedImageView = findViewById(R.id.captured_image_view);

        Intent intent = getIntent();
        Uri capturedImageUri = intent.getParcelableExtra("capturedImage");

        capturedImageView.setImageURI(capturedImageUri);
    }
}
