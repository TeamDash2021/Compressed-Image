package com.custom.compressimage;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Arrays;

public class HomeScreen extends AppCompatActivity {
    private Vibrator myVib;
    ImageButton compress, crop, isettings;
    private static final String AD_UNIT_ID = "ca-app-pub-8529411231000751/5583136226";
    private static final String TAG = "HomeScreen";
    private FrameLayout adContainerView;
    private AdView adView;
    private InterstitialAd mInterstitialAd;

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
        isettings = findViewById(R.id.setting_btn);

        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;


        compress.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, MainActivity.class);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(HomeScreen.this);
            startActivity(intent, options.toBundle());
            myVib.vibrate(30);
            Toast toast = Toast.makeText(context, "Happy Compressing", duration);
            toast.show();
        });


        crop.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, ImageCropping.class);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(HomeScreen.this);
            startActivity(intent, options.toBundle());
            if (mInterstitialAd != null) {
                mInterstitialAd.show(HomeScreen.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
            myVib.vibrate(30);
            Toast toast = Toast.makeText(context, "Let's Crop", duration);
            toast.show();
        });

        isettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, AboutActivity.class);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(HomeScreen.this);
            startActivity(intent, options.toBundle());
            myVib.vibrate(30);
            Toast toast = Toast.makeText(context, "Enjoy", duration);
            toast.show();

            if (mInterstitialAd != null) {
                mInterstitialAd.show(HomeScreen.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        });

        //AD View Code Starts Here
        // Log the Mobile Ads SDK version.
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {}
        });

        loadAd();

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345")).build());

        adContainerView = findViewById(R.id.ad_view_container);

        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        adContainerView.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-8529411231000751/6314598114", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        HomeScreen.this.mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        HomeScreen.this.mInterstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        HomeScreen.this.mInterstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }
                });
    }



    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void loadBanner() {
        // Create an ad request.
        adView = new AdView(this);
        adView.setAdUnitId(AD_UNIT_ID);
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
}
