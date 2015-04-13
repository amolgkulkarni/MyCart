package eci.officeshopper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import eci.officeshopper.util.Config;
import eci.officeshopper.util.ImageDownloader;

/**
 * Splash-screen activity that displays Dealer logo for brief time till required data is available.
 */
public class SplashActivity extends Activity {
    private static final String TAG = "OfficeShopper";
    private ImageView mSplashImg;
    // Splash screen timer. Splash screen is shown at least for some time for dealer branding
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        // View showing Splash image
        mSplashImg = (ImageView) findViewById(R.id.imgSplash);

        SharedPreferences preferences = getSharedPreferences(Config.PREF_MODULE, MODE_PRIVATE);
        final Integer result = preferences.getInt(Config.DEALER_AUTH_STATE, Config.AuthState.STATE_NONE.ordinal());

        if (result > Config.AuthState.STATE_ACCEPTED.ordinal()) {
            // Download from stored splash url in case cache is cleared
            String dealerSplashUrl = preferences.getString(Config.DEALER_SPLASH_URL, null);
            ImageDownloader imageDownloader = new ImageDownloader(this, mSplashImg,
                    Config.DEALER_SPLASH_IMG, R.drawable.splash, 0){
                @Override
                public boolean onExecute(Bitmap bm) {
                    showActivity(result);
                    return true;
                }
            };
            imageDownloader.execute(dealerSplashUrl);
        } else {
            showActivity (result);
        }
    }

    private void showActivity (Integer result) {
        final Intent i;
        switch (result) {
            case 0:
                i = new Intent(SplashActivity.this, TermsActivity.class);
                break;
            case 1:
                i = new Intent(SplashActivity.this, AuthActivity.class);
                break;

            case 2:
                i = new Intent(SplashActivity.this, LoginActivity.class);
                break;

            case 3:
                i = new Intent(SplashActivity.this, HomeActivity.class);
                break;

            default:
                Toast.makeText(getApplicationContext(), "Something went wrong !",
                        Toast.LENGTH_SHORT).show();
                i = new Intent(SplashActivity.this, TermsActivity.class);
                break;
        }

        // Make sure, splash screen is visible for some time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run () {
                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

}
