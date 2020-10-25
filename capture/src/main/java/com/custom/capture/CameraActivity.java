package com.custom.capture;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.custom.capture.spinnycamera.BaseSpinnyCameraActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class CameraActivity extends BaseSpinnyCameraActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final String LOG_TAG = CameraActivity.class.getSimpleName();
    private String photoName;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getBundleData();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPhotoTaken(byte[] data) {
        showImagePreviewDialog(data);
    }

    // Show photo preview
    private void showImagePreviewDialog(final byte[] bitmapData) {
        // Create bitmap from data
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // Set dialog view
        dialog.setContentView(R.layout.view_photo_preview);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.imv_photo_preview);
        imageView.setImageBitmap(bitmap);

        // Ok button listener
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok_photo_dialog);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap(bitmapData);
                dialog.dismiss();
                //finish();
            }
        });

        // Discard button listener
        Button btn_discard = (Button) dialog.findViewById(R.id.btn_discard_photo_dialog);
        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Show dialog
        dialog.show();
    }

    /**
     * Method to save bitmap data to file.
     *
     * @param data byte array to be saved.
     */
    private void saveBitmap(byte[] data) {
        File f = generatePhotoFile();
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(f));
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            DebugHandler.logException(e);
        }
    }

    /**
     * Method to generate file name.
     *
     * @return file name.
     */
    private File generatePhotoFile() {
        File outputDir = getPhotoDirectory();
        File photoFile = null;
        if (outputDir != null) {
            String photoFileName = (photoName==null||photoName.isEmpty())?"IMG" + "_" + System.currentTimeMillis() + ".jpg":photoName;
            photoFile = new File(outputDir, photoFileName);
        }
        return photoFile;
    }

    private File getPhotoDirectory() {
        File outputDir = null;
        if (photoPath!=null&&!photoPath.isEmpty()){
            outputDir=new File(photoPath);
            return outputDir;
        }
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(pictureDir, String.format("Spinny/Refurb"));
            if (!outputDir.exists()) {
                if (!outputDir.mkdirs()) {
                    Toast.makeText(this, "Failed to create directory :" + outputDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        return outputDir;
    }

    // get data from Activity Intent
    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        photoPath=getIntent().getStringExtra("image_file_path");
        photoName=getIntent().getStringExtra("image_file_name");
    }
}

