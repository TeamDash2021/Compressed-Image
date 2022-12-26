package com.custom.compressimage;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class HomeScreen extends AppCompatActivity {
    private Vibrator myVib;
    ImageButton compress, crop, isettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        ActivityCompat.requestPermissions(HomeScreen.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(HomeScreen.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        setContentView(R.layout.homescreen_activity);

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        compress = findViewById(R.id.compressButton);
        crop = findViewById(R.id.cropButton);

        //isettings = findViewById(R.id.setting_btn);
        final Context context = getApplicationContext();
        final CharSequence text = "ENJOY";
        final int duration = Toast.LENGTH_SHORT;


        compress.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, MainActivity.class);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(HomeScreen.this);
            startActivity(intent, options.toBundle());
            myVib.vibrate(50);
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        });


        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ImageCropping.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(HomeScreen.this);
                startActivity(intent, options.toBundle());
                myVib.vibrate(50);
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        /*
        isettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoGuide_act.this, Third_act.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(InfoGuide_act.this);
                startActivity(intent, options.toBundle());
                myVib.vibrate(50);
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
*/
    }

}
