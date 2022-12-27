package com.custom.compressimage;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageCropping extends AppCompatActivity {

    ImageView imageView;
    Button save,pickButton;
    Uri resultUri;
    File file;
    SimpleDateFormat dateFormatter;
    public static final String DATE_FORMAT = "yyyyMMdd_HHmm";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropping);

        dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        save = findViewById(R.id.saveImage);
        pickButton = findViewById(R.id.pickCropping);
        imageView = findViewById(R.id.CropedImage);

        pickButton.setOnClickListener(view -> {
            try {
                startCropActivity();
            }
            catch (Exception e)
            {
                Toast.makeText(ImageCropping.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        try{
            file = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Cropped Images");
            if (!file.exists()) {
                file.mkdirs();
            }}
        catch(Error e){
            Toast.makeText(ImageCropping.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        save.setOnClickListener(view -> {

            try {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

                String root = Environment.getExternalStorageDirectory().toString();
                File file = new File(root + "/DCIM/Cropped Images/img_"+dateFormatter.format(new Date())+".jpg");

                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                Toast.makeText(ImageCropping.this,"Image Saved",Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(ImageCropping.this,"Please select an image first",Toast.LENGTH_SHORT).show();
            }
        });
        
    }


    private void startCropActivity()
    {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}