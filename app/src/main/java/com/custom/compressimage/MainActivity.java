package com.custom.compressimage;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private static final int PICK_CAMERA_IMAGE = 2;
    private static final int PICK_GALLERY_IMAGE = 1;

    public static final String DATE_FORMAT = "yyyyMMdd_HHmm";
    //public static final String IMAGE_DIRECTORY = "ImageScalling";

    ImageButton btnGallery, btnCamera;
    Button btnCompress;
    ImageView img, imgCompress;
    Uri imageCaptureUri;
    File file, sourceFile, destFile;
    SimpleDateFormat dateFormatter;
    AutoCompleteTextView autoCompleteTextView;
    TextView txtCompSize, txtOriginalSize;
    int qualityVal;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.VIBRATE},1);
        //ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        //get the spinner from the xml.
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);


        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.imgquality, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        autoCompleteTextView.setAdapter(staticAdapter);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem=autoCompleteTextView.getAdapter().getItem(position).toString();
                qualityVal = Integer.parseInt(selectedItem);
                Toast.makeText(getApplicationContext(),selectedItem ,  Toast.LENGTH_SHORT).show();
            }
        });

        try{
        file = new File(android.os.Environment.getExternalStorageDirectory()
                + "/" + DIRECTORY_PICTURES, "/Compressed Image");
        if (!file.exists()) {
            file.mkdirs();
        }}
        catch(Error e){
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        dateFormatter = new SimpleDateFormat(DATE_FORMAT);

        initView();
    }

    public void initView() {
        btnGallery = findViewById(R.id.activity_main_btn_load_from_gallery);
        btnCamera = findViewById(R.id.activity_main_btn_load_from_camera);
        btnCompress = findViewById(R.id.activity_main_btn_compress);
        img = findViewById(R.id.activity_main_img);
        imgCompress = findViewById(R.id.activity_main_img_compress);
        txtCompSize = findViewById(R.id.newSizeTxt);
        txtOriginalSize = findViewById(R.id.originalSizeTxt);
        btnGallery.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnCompress.setOnClickListener(this);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        try{
        switch (v.getId()) {
            case R.id.activity_main_btn_load_from_gallery:
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                Intent intentGalley = new Intent(Intent.ACTION_PICK);
                intentGalley.setType("image/*");
                startActivityForResult(intentGalley, PICK_GALLERY_IMAGE);
                break;
            case R.id.activity_main_btn_load_from_camera:
                //ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                destFile = new File(file, "img_"
                        + dateFormatter.format(new Date()) + ".jpeg");
                imageCaptureUri = Uri.fromFile(destFile);

                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                startActivityForResult(intentCamera, PICK_CAMERA_IMAGE);
                break;

            case R.id.activity_main_btn_compress:
                Bitmap bmp = decodeFile(destFile);
                imgCompress.setImageBitmap(bmp);
                try{
                txtCompSize.setText("Size: " + Formatter.formatShortFileSize(this, destFile.length()));
                txtOriginalSize.setText("Size: " + Formatter.formatShortFileSize(this, sourceFile.length()));}
                catch(Exception e){
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Compressed & Saved",Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,String.valueOf(qualityVal),Toast.LENGTH_LONG).show();
                break;
        }}
        catch (Error e){
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_GALLERY_IMAGE:
                    Uri uriPhoto = data.getData();
                    Log.d(TAG + ".PICK_GALLERY_IMAGE", "Selected image uri path :" + uriPhoto.toString());

                    img.setImageURI(uriPhoto);

                    sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));

                    destFile = new File(file, "img_"
                            + dateFormatter.format(new Date()) + ".png");

                    Log.d(TAG, "Source File Path :" + sourceFile);

                    try {
                        copyFile(sourceFile, destFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case PICK_CAMERA_IMAGE:
                    Log.d(TAG + ".PICK_CAMERA_IMAGE", "Selected image uri path :" + imageCaptureUri);
                    img.setImageURI(imageCaptureUri);
                    break;
            }
        }}
        catch (Error e){
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        int IMAGE_MAX_SIZE = qualityVal;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        try {
            FileOutputStream out = new FileOutputStream(destFile);
            if (b != null) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            out.flush();
            out.close();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return b;
    }

    /**
     * This is useful when an image is not available in sdcard physically but it displays into photos application via google drive(Google Photos) and also for if image is available in sdcard physically.
     * @param uriPhoto
     * @return
     */

    public String getPathFromGooglePhotosUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(this);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {

        try{
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source;
        FileChannel destination;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }}
        catch(Error e){
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

}